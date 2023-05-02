import numpy as np
import librosa

def calculate_mode(audio, sr):
    # Extract the chromagram from the audio signal
    chromagram = librosa.feature.chroma_cqt(y= audio, sr=sr)

    # Compute the mode using the chromagram
    mode = int(np.argmax(chromagram.mean(axis=1)))

    return mode
