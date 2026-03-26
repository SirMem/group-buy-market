---
description: Plays a notification sound to grab my attention
allowed-tools: Bash
---
I need you to play a notification sound right now. 

Please detect my operating system and execute the correct command using your Bash tool:
- Mac: `afplay /System/Library/Sounds/Glass.aiff &`
- Linux: `paplay /usr/share/sounds/freedesktop/stereo/complete.oga &`
- Windows: `powershell.exe -c "(New-Object Media.SoundPlayer 'C:\Windows\Media\notify.wav').PlaySync()" &`

Just run the command in the background and reply with "Ring ring! 📞". Do not explain the steps.