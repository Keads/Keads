from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
from flask import Flask, request, jsonify
import librosa
import pandas as pd
from sklearn.neighbors import NearestNeighbors
import numpy as np
from feature_extraction.acousticness import calculate_acousticness
from feature_extraction.valence import calculate_valence
from feature_extraction.liveness import calculate_liveness
from feature_extraction.instrumentalness import calculate_instrumentalness
from feature_extraction.speechiness import calculate_speechiness
from feature_extraction.loudness import calculate_loudness
from feature_extraction.key import calculate_key
from feature_extraction.energy import calculate_energy
from feature_extraction.danceability import calculate_danceability
from feature_extraction.tempo import calculate_tempo
from feature_extraction.duration import calculate_duration
from feature_extraction.mode import calculate_mode


app = Flask(__name__)

data = pd.read_csv('dataset/data.csv')

columns_to_keep = ['name', 'acousticness', 'energy', 'valence', 'liveness',
                   'instrumentalness', 'speechiness', 'loudness', 'key',
                   'danceability', 'tempo', 'duration_ms', 'mode']

# Get the feature columns
feature_columns = data[columns_to_keep[1:]]

# Convert feature columns to a numpy array
X_train = feature_columns.to_numpy().astype(float)

# Create and train the KNN model
knn_model = NearestNeighbors(n_neighbors=5)  # Adjust the number of neighbors as desired
knn_model.fit(X_train)


@app.route('/recommend_tracks/<track_name>', methods=['GET'])
def recommend_tracks(track_name):
    # Find the track index in the data
    track_index = data[data['name'] == track_name].index
    if len(track_index) > 0:
        query_features = X_train[track_index]

        # Find the k nearest neighbors
        _, indices = knn_model.kneighbors(query_features)

        # Get the names of the recommended tracks
        recommended_tracks = data.loc[indices[0], 'name']

        return jsonify(recommended_tracks.tolist())
    else:
        return jsonify({'error': 'Track not found.'})




# MongoDB connection details
mongo_uri = "mongodb://localhost:32782"
database_name = "StreamDB"
# Connect to MongoDB
client = MongoClient(mongo_uri)
database = client[database_name]
fs = GridFS(database)
# ObjectId of the file in GridFS
@app.route('/process_file/<file_id>', methods=['GET'])
def process_file(file_id):
    try:
        # Convert the file_id to an ObjectId
        file_object_id = ObjectId(file_id)

        # Retrieve the file from GridFS
        file_object = fs.find_one({"_id": file_object_id})

        if file_object:
            # Get the filename
            filename = file_object.filename

            # Call the functions to calculate features
            audio, sr = librosa.load(file_object)
            acousticness = calculate_acousticness(audio, sr)
            valence = calculate_valence(audio, sr)
            liveness = calculate_liveness(audio, sr)
            instrumentalness = calculate_instrumentalness(audio, sr)
            speechiness = calculate_speechiness(audio, sr)
            loudness = calculate_loudness(audio, sr)
            key = calculate_key(audio, sr)
            energy = calculate_energy(audio, sr)
            danceability = calculate_danceability(audio, sr)
            tempo = calculate_tempo(audio, sr)
            duration = calculate_duration(audio, sr)
            mode = calculate_mode(audio, sr)

            # Prepare the results
            if acousticness is not None:
                results = {
                    "filename": filename,
                    "acousticness": float(acousticness),
                    "energy": float(energy),
                    "valence": float(valence),
                    "liveness": float(liveness),
                    "instrumentalness": float(instrumentalness),
                    "speechiness": float(speechiness),
                    "loudness": float(loudness),
                    "key": float(key),
                    "danceability": float(danceability),
                    "tempo": float(tempo),
                    "duration": float(duration),
                    "mode": float(mode)
                }
                return results
            else:
                return {"error": "Failed to calculate features."}
        else:
            return {"error": "File not found."}
    except Exception as e:
        return {"error": str(e)}



@app.route('/recommend_track/<file_id>', methods=['GET'])
def recommend_track(file_id):
    # Retrieve the file from GridFS
    file_object = fs.find_one({"_id": ObjectId(file_id)})
    if file_object:
        # Read the audio file and extract features
        audio, sr = librosa.load(file_object)
        query_features = np.array([
            calculate_acousticness(audio, sr),
            calculate_energy(audio, sr),
            calculate_valence(audio, sr),
            calculate_liveness(audio, sr),
            calculate_instrumentalness(audio, sr),
            calculate_speechiness(audio, sr),
            calculate_loudness(audio, sr),
            calculate_key(audio, sr),
            calculate_danceability(audio, sr),
            calculate_tempo(audio, sr),
            calculate_duration(audio, sr),
            calculate_mode(audio, sr)
        ], dtype=float).reshape(1, -1)

        # Find the k nearest neighbors
        _, indices = knn_model.kneighbors(query_features)

        # Get the names of the recommended tracks
        recommended_tracks = data.loc[indices[0], 'name']

        return jsonify(recommended_tracks.tolist())
    else:
        return jsonify({'error': 'File not found.'})




if __name__ == '__main__':
    app.run()