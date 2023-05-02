import librosa

def calculate_duration(audio, sr):
    # Calculate the duration of the audio in seconds
    duration = librosa.get_duration(y = audio, sr=sr)
    
    # Convert the duration to milliseconds
    duration_ms = duration * 1000
    
    # Return the duration in milliseconds
    return int(round(duration_ms, 0))
