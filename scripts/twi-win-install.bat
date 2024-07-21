@echo off
SET TWI_HOME="%~dp0\twi"
:: Check Prerequisites
echo Administrative permissions required. Detecting permissions...

net session >nul 2>&1
if %errorLevel% == 0 (
    echo Success..
    where java >nul 2>nul
	if %errorlevel%==1 (
	    echo Failure: Java not found in path.Requires Java 9 or higher
	    echo Press any key to exit..
	) else (
		:: Set Java variable
		setx TWI_HOME "%TWI_HOME%" /m
		
		:: Set Path variable
		setx PATH "%PATH%;%TWI_HOME%" /m
		echo Successfully installed twi..
		echo Press any key to exit..
	)
    
) else (
    echo Failure: Not in Admin Mode. Please start again as Adminsistrator 
    echo Press any key to exit..
)
pause >nul