$(document).ready(function(){
    let token = getCookie('session-id');
    if (token) {
        const payload = parseJWT(token);
        $('#name').text(payload.name)
    } else {
        console.log('No JWT token found in cookies');
    }

    $('#logout').on('click', function(e){
        e.preventDefault();
        if (confirm("로그아웃 하시겠습니까?")) {
            deleteCookie('session-id', '/');
            window.location.href = '/login';
        }
    })

})

function deleteCookie(cookieName, path, domain) {
    document.cookie = cookieName + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=' + (path || '/') + '; domain=' + (domain || window.location.hostname) + ';';
}