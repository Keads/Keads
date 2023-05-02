import numpy as np

def calculate_speechiness(audio, sr):
    # Calculate the speechiness feature
    speechiness = np.mean(np.abs(audio))

    # Scale the speechiness between 0 and 1
    scaled_speechiness = (speechiness - np.min(np.abs(audio))) / (np.max(np.abs(audio)) - np.min(np.abs(audio)))

    # Format the scaled speechiness to three decimal places
    formatted_speechiness = round(scaled_speechiness, 3)

    # Return the formatted speechiness
    return formatted_speechiness
