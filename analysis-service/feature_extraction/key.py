import librosa
import numpy as np

def calculate_key(audio, sr):
    # Extract the chroma feature
    chroma = librosa.feature.chroma_stft(y=audio, sr=sr)

    # Calculate the mean of the chroma feature
    chroma_mean = np.mean(chroma, axis=1)

    # Find the key with the maximum value in the chroma feature
    key_index = np.argmax(chroma_mean)

    # Return the key index
    return key_index