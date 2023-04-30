from pymongo import MongoClient
from bson.objectid import ObjectId

# Connect to MongoDB
client = MongoClient("mongodb://localhost:32768")
db = client["StreamDB"]
fs = db["fs.files"]

def get_file_details(file_id):
    file_doc = fs.find_one({"_id": ObjectId(file_id)})
    if file_doc:
        filename = file_doc.get("filename")
        filesize = file_doc.get("length")
        print("File Name: ", filename)
        print("File Size: ", filesize, "bytes")
    else:
        print("File not found.")

# Usage example
file_id = "6449321899fb4332808713c1"
get_file_details(file_id)
