import librosa

def calculate_tempo(audio, sr):
    # Calculate the tempo of the audio
    tempo, _ = librosa.beat.beat_track(y = audio, sr=sr)
    
    # Return the tempo in beats per minute (bpm)
    return round(tempo, 3)
