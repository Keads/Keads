import librosa
import numpy as np

def calculate_speechiness(audio, sr):
    # Compute the speechiness feature using Librosa
    speechiness = librosa.feature.spectral_contrast(y=audio, sr=sr)

    # Calculate the average speechiness
    average_speechiness = np.mean(speechiness)

    # Scale the average speechiness between 0 and 1
    scaled_speechiness = (average_speechiness - np.min(speechiness)) / (np.max(speechiness) - np.min(speechiness))

    # Format the scaled speechiness to three decimal places
    formatted_speechiness = round(scaled_speechiness, 3)

    # Return the formatted speechiness
    return formatted_speechiness
