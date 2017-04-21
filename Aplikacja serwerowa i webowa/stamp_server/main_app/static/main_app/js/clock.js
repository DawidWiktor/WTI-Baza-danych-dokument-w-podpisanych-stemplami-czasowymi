MonthName = new Array(12);
MonthName[0] = "stycznia";
MonthName[1] = "lutego";
MonthName[2] = "marca";
MonthName[3] = "kwietnia";
MonthName[4] = "maja";
MonthName[5] = "czerwca";
MonthName[6] = "lipca";
MonthName[7] = "sierpnia";
MonthName[8] = "września";
MonthName[9] = "października";
MonthName[10] = "listopada";
MonthName[11] = "grudnia";

function startTime() {
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth();
        var yyyy = today.getFullYear();
        var h = today.getHours();
        var m = today.getMinutes();
        var s = today.getSeconds();
        dd = checkTime(dd);
        mm = MonthName[mm];
        m = checkTime(m);
        s = checkTime(s);
        document.getElementById('clock').innerHTML =
        dd+' '+mm+' '+yyyy + ", " + h + ":" + m + ":" + s;
        var t = setTimeout(startTime, 500);
    }
    function checkTime(i) {
        if (i < 10) {i = "0" + i}  // add zero in front of numbers < 10
        return i;
    }
