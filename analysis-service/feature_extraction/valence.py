import numpy as np


def calculate_valence(audio, sr):
    # Calculate the valence feature
    valence = np.mean(audio)

    # Scale the valence between 0 and 1
    scaled_valence = (valence - np.min(audio)) / (np.max(audio) - np.min(audio))

    # Format the scaled valence to three decimal places
    formatted_valence = round(scaled_valence, 3)

    # Return the formatted valence
    return formatted_valence
