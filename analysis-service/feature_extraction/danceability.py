import numpy as np
import librosa

def calculate_danceability(audio, sr):
    # Calculate the tempo of the audio
    tempo, _ = librosa.beat.beat_track(y = audio, sr=sr)
    
    # Calculate the danceability based on tempo
    danceability = min(1, tempo / 200)
    
    # Return danceability with three decimal places
    return round(danceability, 3)
