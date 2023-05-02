from pymongo import MongoClient
from gridfs import GridFS
from bson import ObjectId
from feature_extraction.acousticness import calculate_acousticness

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

    # Call the function and get the result
    acousticness = calculate_acousticness(file_object)

    # Print the result
    if acousticness is not None:
        print("Filename:", filename)
        print("Acousticness:", acousticness)
    else:
        print("Failed to calculate acousticness.")
else:
    print("File not found.")
