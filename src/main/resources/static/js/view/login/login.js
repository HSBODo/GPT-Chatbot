
// Enter 키 입력 이벤트
$(document).on('keydown', function(event) {
    if (event.key === 'Enter') {
        $('#login').click();
    }
});

$('#login').on('click', function(){
    login()
})

function login() {
    var id = $('#id').val();
    var password = $('#password').val();

    if (!id) {
        alert("아이디를 입력해주세요.")
        $('#id').focus()
        return
    }

    if (!password) {
        alert("비밀번호를 입력해주세요.")
        $('#password').focus()
        return
    }

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: id, password: password })
    })
    .then(response => {

        if (!response.ok) {
            throw new Error('Login failed');
        }

        window.open(response.url, "_self");
    })
    .catch(error => {
        alert("계정 정보가 올바르지 않습니다.")
    });
}