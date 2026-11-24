@echo off
python -m pip install pywebview pyinstaller playwright
python -m PyInstaller --onefile --windowed --collect-all playwright --name Mishu --add-data "..\app\src\main\assets\index.html;app/src/main/assets" Mishu.py
pause
