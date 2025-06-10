$(document).ready(function() {
    getAssistant()
    function getAssistant() {
        fetch(`/ai/assistants`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            $("#instructionsTextarea").val(data.prompt)
        })
            .catch(error => {
                console.log(error)
            });
    }
})

function save() {
    if (confirm("프롬프트를 저장하시겠습니까?")) {
        const prompt = $("#instructionsTextarea").val();

        fetch(`/ai/assistants/prompt`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ prompt: prompt })  // prompt 데이터를 JSON으로 보내기
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                // 서버에서 리턴한 prompt를 textarea에 다시 세팅
                $("#instructionsTextarea").val(data.prompt);
                alert("저장을 완료하였습니다.")
            })
            .catch(error => {
                alert("저장을 실패하였습니다.")
                console.error('Error:', error);
            });
    }

}
