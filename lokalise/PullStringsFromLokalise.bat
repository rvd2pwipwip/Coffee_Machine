@ECHO OFF

:: ================================================================
:: VARIABLES TO CHANGE FOR YOUR PROJECT ARE HERE!!!
:: ================================================================

:: Found in "API tokens" on lokalise.co/profile.
:: this token was taken from chris fournier account.
SET "LOKALISE_API_TOKEN=6e633f031cbae101a3d823a8b8635a543c0e92f1"

:: Found in "General" on your project's settings page.
:: Example: https://lokalise.co/settings/554777705a2efae66fa281.08541799/.
SET "LOKALISE_PROJECT_ID=364997415e1e12509eec85.81030034"

:: Path to strings directory within the project.
:: This is where downloaded string files will be output.
SET "PATH_TO_PROJECT_ROOT=%cd%"
SET "PATH_TO_STRINGS=%PATH_TO_PROJECT_ROOT%



:: ================================================================
:: PROGRAM STARTS HERE
:: ================================================================

TITLE Pull strings from Lokalise

ECHO Pull strings from Lokalise started.

ECHO .
ECHO Validate required executables.
where /q lokalise2 || (
	COLOR FC
	ECHO [ERROR] Could not find lokalize.exe!
	ECHO ....... Help:
	ECHO .......  * Download and unzip lokalize.tar.gz from https://github.com/lokalise/lokalise-cli-2-go
	ECHO .......  * Add lokalize to your "Path" environment variables.
	PAUSE
	COLOR 07
	EXIT
)
ECHO lokalise.exe OK

ECHO .
ECHO Import resources

ECHO .
        
lokalise2 file download ^
        --token %LOKALISE_API_TOKEN% ^
        --project-id %LOKALISE_PROJECT_ID% ^
        --original-filenames=false ^
        --bundle-structure "values-%%LANG_ISO%%/strings.xml"  ^
        --unzip-to %PATH_TO_STRINGS% ^
        --format xml ^
        --include-description ^
        --export-sort a_z ^
        --export-empty-as skip
ECHO .

ECHO .
ECHO Pull strings from Lokalise ended!
PAUSE
EXIT