import numpy as np
import librosa

def calculate_danceability(audio, sr):
    # Calculate the tempo of the audio
    tempo, _ = librosa.beat.beat_track(audio, sr=sr)
    
    # Calculate the danceability based on tempo
    danceability = min(1, tempo / 200)
    
    return danceability
