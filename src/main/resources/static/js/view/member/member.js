$(document).ready(function(){
    const headers = [
        'No', 'ID','이름', '연락처','가입일','관리'
    ];

    getMembers()

    $("#addMemberButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').modal('show');
    })

    $("#addMember").on("click",function () {
        const id = $("#addMemberId").val();
        const password = $("#addMemberPassword").val();
        const name = $("#addMemberName").val();
        const phone = $("#addMemberPhone").val();

        if (!id) {
            alert("아이디를 입력하세요.")
            return
        }

        if (!password) {
            alert("비밀번호를 입력하세요.")
            return
        }

        if (!name) {
            alert("이름을 입력하세요.")
            return
        }

        if (!phone) {
            alert("연락처를 입력하세요.")
            return
        }

        addMember(id,password,name,phone)
    })


    $("#updateMember").on("click",function () {
        const id = $("#updateMemberId").val();
        const password = $("#updateMemberPassword").val();
        const name = $("#updateMemberName").val();
        const phone = $("#updateMemberPhone").val();

        if (!id) {
            alert("아이디를 입력하세요.")
            return
        }

        if (!name) {
            alert("이름을 입력하세요.")
            return
        }

        if (!phone) {
            alert("연락처를 입력하세요.")
            return
        }
        updateMember(id,name,phone,password)
    })

    function updateMember(id,name,phone,password) {
        const addData = JSON.stringify({
            id:id,
            name:name,
            phone:phone,
            password:password
        });

        fetch(`/members`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body:addData
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 수정하였습니다.")
                    $('#updateModal').modal('hide');
                    getMembers()
                }else {
                    alert("수정을 실패하였습니다.")
                }
            })
            .catch(error => {

                console.log(error)
            });
    }
    function addMember(id,password, name,phone) {
        const addData = JSON.stringify({
            id:id,
            password:password,
            name:name,
            phone:phone,
        });

        fetch(`/members`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:addData
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 추가하였습니다.")
                    $('#addModal').modal('hide');
                    getMembers()
                }else if(response.status == 403){
                    alert("중복된 ID 입니다.")
                }else {
                    alert("추가를 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }
    function getMembers() {
        fetch(`/members/list`, {
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
    function createTableRow(data, index) {
        const idCell = $('<td>').text(data.id).css({ cursor: 'pointer', color: 'blue' });

        idCell.on('click', () => {
            // 모달에 데이터 채우기
            $('#updateMemberId').val(data.id);
            $('#updateMemberName').val(data.name);
            $('#updateMemberPhone').val(data.phone);
            // 모달 열기
            $('#updateModal').modal('show');
        });

        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        row.append(idCell);
        row.append($('<td>').text(data.name));
        row.append($('<td>').text(data.phone));
        row.append($('<td>').text(data.createDate));
        row.append($('<td style="display: flex; justify-content: center;">').append(deleteButton(data)));

        return row;
    }
    function deleteButton (data) {
        return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){

            if (confirm(`${data.id}계정을 삭제하시겠습니까?`)) {
                deleteMember(data.id)
            }
        })
    }
    function deleteMember(id) {
        fetch(`/members/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 삭제하였습니다.")
                    getMembers()
                }else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


