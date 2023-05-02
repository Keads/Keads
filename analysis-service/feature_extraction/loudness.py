import librosa
import numpy as np

def calculate_loudness(audio, sr):
    # Compute the loudness using Librosa
    loudness = librosa.amplitude_to_db(librosa.feature.rms(y=audio), ref=np.max)

    # Calculate the average loudness
    average_loudness = np.mean(loudness)

    # Scale the average loudness between -60 and 0
    scaled_loudness = (average_loudness - np.min(loudness)) / (np.max(loudness) - np.min(loudness))
    scaled_loudness = scaled_loudness * 60 - 60

    # Round the scaled loudness to three decimal places
    formatted_loudness = round(scaled_loudness, 3)

    # Return the formatted loudness value
    return formatted_loudness
