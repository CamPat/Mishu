@echo off
python -m pip install pywebview pyinstaller
python -m PyInstaller --onefile --windowed --name Mishu --add-data "..\app\src\main\assets\index.html;app/src/main/assets" Mishu.py
pause
