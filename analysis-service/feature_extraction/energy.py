import numpy as np

def calculate_energy(audio, sr):
    # Replace nan values with zeros
    audio[np.isnan(audio)] = 0.0
    
    # Calculate the squared amplitude of the audio signal
    squared_audio = np.square(audio)
    
    # Calculate the energy by summing the squared values
    energy = np.sum(squared_audio)
    
    # Determine the scaling factor for normalization
    scaling_factor = 1e6  # Adjust this value based on your dataset
    
    # Normalize the energy to a value between 0 and 1
    normalized_energy = energy / scaling_factor
    
    # Clamp the normalized energy value between 0 and 1
    normalized_energy = max(0.0, min(1.0, normalized_energy))
    
    # Round the normalized energy to three decimal places
    normalized_energy = round(normalized_energy, 3)
    
    return normalized_energy
