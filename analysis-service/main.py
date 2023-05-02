from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
import librosa
import numpy as np



# MongoDB connection details
mongo_uri = "mongodb://localhost:32768"
database_name = "StreamDB"

# Connect to MongoDB
client = MongoClient(mongo_uri)
database = client[database_name]
fs = GridFS(database)

# ObjectId of the file in GridFS
file_object_id = ObjectId("64440a2efe7e7a2bee86f332")

def calculate_acousticness(file_object_id):
    # Retrieve the file from GridFS
    file_object = fs.find_one({"_id": file_object_id})

    if file_object:
        # Get the filename
        filename = file_object.filename

        # Load audio data using LibROSA
        audio, sr = librosa.load(file_object)

        # Compute the acousticness feature
        acousticness = librosa.feature.spectral_centroid(y=audio, sr=sr)

        # Calculate the average acousticness
        average_acousticness = np.mean(acousticness)

        # Scale the average acousticness between 0 and 1
        scaled_acousticness = (average_acousticness - np.min(acousticness)) / (np.max(acousticness) - np.min(acousticness))

        # Format the scaled acousticness to three decimal places
        formatted_acousticness = round(scaled_acousticness, 3)

        # Return the filename and formatted acousticness
        return filename, formatted_acousticness
    else:
        return None, None

# Call the function and get the result
filename, acousticness = calculate_acousticness(file_object_id)

# Print the result
if filename and acousticness is not None:
    print("Filename:", filename)
    print("Acousticness:", acousticness)
else:
    print("File not found.")