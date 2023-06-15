from flask import Flask, jsonify, request
from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
import librosa
from confluent_kafka import Consumer
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

# Initialize Flask app
app = Flask(__name__)

# MongoDB connection details
mongo_uri = "mongodb://localhost:32768"
database_name = "StreamDB"

# Connect to MongoDB
client = MongoClient(mongo_uri)
database = client[database_name]
fs = GridFS(database)


@app.route('/features', methods=['POST'])
def process_track_features():
    track_id = request.form.get('track_id')
    if track_id:
        # Process the received track ID
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

            document = info_collection.find_one({"song": track_id})
            if document:
                # Update the document in 'info' collection with the calculated features
                info_collection.update_one({"_id": document["_id"]}, {"$set": {
                    "acousticness": acousticness,
                    "valence": valence,
                    "liveness": liveness,
                    "instrumentalness": instrumentalness,
                    "speechiness": speechiness,
                    "loudness": loudness,
                    "key": key,
                    "energy": energy,
                    "danceability": danceability,
                    "tempo": tempo,
                    "duration": duration,
                    "mode": mode
                }})
            else:
                print(f"No document found for track ID: {track_id}")

        return "Track features processed successfully"
    else:
        return "No track ID provided in the request"



if __name__ == '__main__':
    app.run()
