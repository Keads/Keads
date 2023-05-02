import librosa
import numpy as np

def calculate_acousticness(file_object):
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

    # Return the formatted acousticness
    return formatted_acousticness
