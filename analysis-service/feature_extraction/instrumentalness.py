import numpy as np

def calculate_instrumentalness(audio, sr):
    # Calculate the instrumentalness feature
    instrumentalness = np.mean(np.abs(np.diff(audio)))

    # Scale the instrumentalness between 0 and 1
    scaled_instrumentalness = (instrumentalness - np.min(np.abs(np.diff(audio)))) / (np.max(np.abs(np.diff(audio))) - np.min(np.abs(np.diff(audio))))

    # Format the scaled instrumentalness to three decimal places
    formatted_instrumentalness = round(scaled_instrumentalness, 3)

    # Return the formatted instrumentalness
    return formatted_instrumentalness
