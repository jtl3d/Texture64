FOR %%A IN (%*) DO (
	java -jar "%~dp0Texture64.jar" %%A true
	move "%%~dpAout.rgba5551" "%%~dpnA.rgba5551"
	move "%%~dpAout.rgba8888" "%%~dpnA.rgba8888"
	move "%%~dpAout.ia8" "%%~dpnA.ia8"
	move "%%~dpAout.i8" "%%~dpnA.i8"
	move "%%~dpAout.ia8file" "%%~dpnA.ia8file"
	move "%%~dpAout.ci4pal" "%%~dpnA.ci4pal"
	move "%%~dpAout.ci4tex" "%%~dpnA.ci4tex"
	java -jar "%~dp0Texture64.jar" %%A false
	move "%%~dpAout.rgba5551" "%%~dpnA-nointerleave.rgba5551"
	move "%%~dpAout.rgba8888" "%%~dpnA-nointerleave.rgba8888"
	move "%%~dpAout.ia8" "%%~dpnA-nointerleave.ia8"
	move "%%~dpAout.i8" "%%~dpnA-nointerleave.i8"
	move "%%~dpAout.ia8file" "%%~dpnA-nointerleave.ia8file"
	move "%%~dpAout.ci4pal" "%%~dpnA.ci4pal"
	move "%%~dpAout.ci4tex" "%%~dpnA.ci4tex"
)
pause