var hybridAppVersionString = "1.0";


function clickDownload() {
    window.native.downloadUpdates();
    window.location.reload();
}

function clickDeleteUpdates() {
    window.native.deleteUpdates();
    window.location.reload();
}

function initialize() {
    if (window.native.isUpdateAvailable(hybridAppVersionString)) {
        document.getElementById("downloadButton").style.visibility = "visible";
        document.getElementById("deleteUpdatesButton").style.visibility = "hidden";
    } else {
        document.getElementById("downloadButton").style.visibility = "hidden";
        document.getElementById("deleteUpdatesButton").style.visibility = "visible";
    }

    document.getElementById("hybridAppVersionString").innerHTML = hybridAppVersionString;
}