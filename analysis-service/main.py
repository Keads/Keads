from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
import librosa
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

# MongoDB connection details
mongo_uri = "mongodb://localhost:32768"
database_name = "StreamDB"

# Connect to MongoDB
client = MongoClient(mongo_uri)
database = client[database_name]
fs = GridFS(database)

# ObjectId of the file in GridFS
file_object_id = ObjectId("64440a2efe7e7a2bee86f332")

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
    instrumentalness = calculate_instrumentalness(audio,sr)
    speechiness = calculate_speechiness(audio, sr)
    loudness = calculate_loudness(audio, sr)
    key = calculate_key(audio, sr)
    energy = calculate_energy(audio, sr)
    danceability = calculate_danceability(audio,sr)
    tempo = calculate_tempo(audio, sr)
    duration = calculate_duration(audio, sr)
    mode = calculate_mode(audio, sr)

    # Print the results
    if acousticness is not None :
        print("Filename:", filename)
        print("Acousticness:", acousticness)
        print("Energy:", energy)
        print("Valence:", valence)
        print("Liveness", liveness)
        print("Instrumentalness", instrumentalness)
        print("Speechiness", speechiness)
        print("Loudness", loudness)
        print("Key", key)
        print("Danceability", danceability)
        print("Tempo", tempo)
        print("Duration", duration)
        print("Mode", mode)
    else:
        print("Failed to calculate features.")
else:
    print("File not found.")