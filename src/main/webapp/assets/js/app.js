// Small helpers shared by all pages.

// any form with data-confirm="..." asks before it is submitted
document.addEventListener('submit', function (e) {
    var msg = e.target.getAttribute('data-confirm');
    if (msg && !window.confirm(msg)) {
        e.preventDefault();
    }
});

// success messages fade away after a few seconds, errors stay
window.addEventListener('load', function () {
    var ok = document.querySelector('.flash.success');
    if (ok) {
        setTimeout(function () {
            ok.style.transition = 'opacity .6s';
            ok.style.opacity = '0';
            setTimeout(function () { ok.remove(); }, 700);
        }, 5000);
    }
});

// colours used by the charts (same as the css variables)
var FT = {
    forest: '#1f4d3a',
    clay: '#d9622b',
    sand: '#d9d2bf',
    palette: ['#1f4d3a', '#d9622b', '#5c8a72', '#e0a458', '#7a6a53', '#3d7ea6', '#a65e7b']
};
