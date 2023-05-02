import numpy as np

def calculate_liveness(audio, sr):
    # Calculate the liveness feature
    liveness = np.mean(np.abs(audio))

    # Scale the liveness between 0 and 1
    scaled_liveness = (liveness - np.min(np.abs(audio))) / (np.max(np.abs(audio)) - np.min(np.abs(audio)))

    # Format the scaled liveness to three decimal places
    formatted_liveness = round(scaled_liveness, 3)

    # Return the formatted liveness
    return formatted_liveness
