function startTime() {
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth()+1;
        var yyyy = today.getFullYear();
        var h = today.getHours();
        var m = today.getMinutes();
        var s = today.getSeconds();
        dd = checkTime(dd);
        mm = checkTime(mm);
        m = checkTime(m);
        s = checkTime(s);
        document.getElementById('clock').innerHTML =
        dd+'.'+mm+'.'+yyyy + ", " + h + ":" + m + ":" + s;
        var t = setTimeout(startTime, 500);
    }
    function checkTime(i) {
        if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
        return i;
    }
