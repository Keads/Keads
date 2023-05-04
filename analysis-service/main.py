from concurrent import futures
import grpc
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

# Import the generated gRPC files
import your_generated_grpc_files as grpc_files

# MongoDB connection details
mongo_uri = "mongodb://localhost:32768"
database_name = "StreamDB"

# Create a class to implement the gRPC server
class FeatureExtractionServicer(grpc_files.FeatureExtractionServiceServicer):
    def __init__(self):
        self.client = MongoClient(mongo_uri)
        self.database = self.client[database_name]
        self.fs = GridFS(self.database)
        self.file_object_ids = []  # List to hold file object IDs

    def ExtractFeatures(self, request, context):
        # Add the file object ID to the list
        self.file_object_ids.append(request.file_object_id)

        if len(self.file_object_ids) > 0:
            # Process the first file object ID in the list
            file_object_id = self.file_object_ids.pop(0)
            file_object = self.fs.find_one({"_id": ObjectId(file_object_id)})

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

                # Create a response message
                response = grpc_files.FeatureExtractionResponse(
                    filename=filename,
                    acousticness=acousticness,
                    energy=energy,
                    valence=valence,
                    liveness=liveness,
                    instrumentalness=instrumentalness,
                    speechiness=speechiness,
                    loudness=loudness,
                    key=key,
                    danceability=danceability,
                    tempo=tempo,
                    duration=duration,
                    mode=mode
                )

                return response

            else:
                # File not found
                return grpc_files.FeatureExtractionResponse()

        else:
            # No more files to process
            return grpc_files.FeatureExtractionResponse()


# Start the gRPC server
def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    grpc_files.add_FeatureExtractionServiceServicer_to_server(
        FeatureExtractionServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()

# Start the gRPC server
if __name__ == '__main__':
    serve()