from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
import librosa
from feature_extraction.acousticness import calculate_acousticness
from feature_extraction.valence import calculate_valence
from feature_extraction.liveness import calculate_liveness
from feature_extraction.instrumentalness import calculate_instrumentalness
from feature_extraction.speechiness import calculate_speechiness


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

    # Print the results
    if acousticness is not None :
        print("Filename:", filename)
        print("Acousticness:", acousticness)
        #print("Energy:", energy)
        print("Valence:", valence)
        print("Liveness", liveness)
        print("Instrumentalness", instrumentalness)
        print("Speechiness", speechiness)
    else:
        print("Failed to calculate features.")
else:
    print("File not found.")