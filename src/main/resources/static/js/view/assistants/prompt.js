$(document).ready(function() {
    getAssistantInfo()
    getTable()
})

function getTable() {
    fetch(`/ai/assistants/prompts`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            return response.json()
        }).then(data => {
        renderTable(data,headers,createTableRow)
    })
        .catch(error => {
            console.log(error)
        });
}
const headers = [
    'No', '제목','프롬프트', '등록일','관리'
];
function createTableRow(data, index) {
    const idCell = $('<td>', {
        text: data.title,
        css: {
            whiteSpace: 'nowrap',
            cursor: 'pointer',
            color: 'blue'
        }
    });

    idCell.on('click', () => {
        // 모달에 데이터 채우기
        $('#updatePromptTitle').val(data.title);
        $('#updatePromptDescription').val(data.prompt);
        $('#updateUUID').val(data.id);
        // 모달 열기
        $('#updateModal').modal('show');
    });

    const row = $('<tr>');
    row.append($('<td>').text(index + 1));
    row.append(idCell);
    row.append($('<td>').text(data.prompt));
    row.append($('<td>').css('white-space', 'nowrap').text(formatDate(data.createDate)));
    row.append($('<td>').append(
        $('<div>')
            .css({
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                gap: '8px' // 버튼 간격
            })
            .append(applyButton(data))
            .append(deleteButton(data))
    ));

    return row;
}

function deleteButton (data) {
    return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){

        if (confirm(`${data.title}을 삭제하시겠습니까?`)) {
            deletePrompt(data.id)
        }
    })
}
function applyButton (data) {
    return  $('<button>').text('적용').addClass('btn btn-success mx-lg-1 btn-sm').on('click',function (){

        if (confirm(`${data.title}을 적용하시겠습니까?`)) {
            applyPrompt(data.id)
        }
    })
}

function addPrompt() {
    const title = $("#addPromptTitle").val();
    const prompt = $("#addPromptDescription").val();

    if (!title) {
        alert("제목을 입력하세요.")
        return
    }

    if (!prompt) {
        alert("프롬프트를 입력하세요.")
        return;
    }

    fetch(`/ai/assistants/prompt`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title : title,
            prompt : prompt
        })  // prompt 데이터를 JSON으로 보내기
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }else {
                alert("등록을 완료하였습니다.")
                getTable()
                $('#addModal').modal('hide');
            }
        })
        .catch(error => {
            alert("등록을 실패하였습니다.")
            console.error('Error:', error);
        })
        .finally(() => {
        });
}
function applyPrompt(id) {
    fetch(`/ai/assistants/prompts/apply/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok){
                alert("성공적으로 적용하였습니다.")
                getTable()
                getAssistantInfo()
            }else {
                alert("적용을 실패하였습니다.")
            }
        })
        .catch(error => {
            console.log(error)
        });
}
function deletePrompt(id) {
    fetch(`/ai/assistants/prompts/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok){
                alert("성공적으로 삭제하였습니다.")
                getTable()
            }else {
                alert("삭제를 실패하였습니다.")
            }
        })
        .catch(error => {
            console.log(error)
        });
}
function updatePrompt() {
    const uuid = $("#updateUUID").val();
    const title = $("#updatePromptTitle").val();
    const prompt = $("#updatePromptDescription").val();

    if (!title) {
        alert("제목을 입력하세요.")
        return
    }

    if (!prompt) {
        alert("프롬프트를 입력하세요.")
        return;
    }
    fetch(`/ai/assistants/prompt`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id : uuid ,
            title : title,
            prompt : prompt
        })  // prompt 데이터를 JSON으로 보내기
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }else {
                alert("수정을 완료하였습니다.")
                getTable()
                $('#updateModal').modal('hide');
            }
        })
        .catch(error => {
            alert("수정을 실패하였습니다.")
            console.error('Error:', error);
        })
        .finally(() => {
        });
}

function getAssistantInfo() {
    $("#loadingOverlay").show();

    fetch(`/ai/assistants`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            $("#instructionsTextarea").val(data.instructions);
        })
        .catch(error => {
            console.error('Error fetching assistant data:', error);
        })
        .finally(() => {
            $("#loadingOverlay").hide();
        });
}
function showAddModal() {
    $("#addPromptTitle").val("");
    $("#addPromptDescription").val("");
    $('#addModal').modal('show');
}

function save() {
    if (confirm("프롬프트를 저장하시겠습니까?")) {
        const prompt = $("#instructionsTextarea").val();
        $("#loadingOverlay").show();
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
                $("#instructionsTextarea").val(data.instructions);
                alert("저장을 완료하였습니다.")
            })
            .catch(error => {
                alert("저장을 실패하였습니다.")
                console.error('Error:', error);
            })
            .finally(() => {
                $("#loadingOverlay").hide();
            });;
    }

}
