$(document).ready(function() {
    let productChart
    const headers = [
        'No', 'ID' ,'이름','연락처','주소','상품명', '링크','상품가격', '당첨일','상태', '지급 상태','기타'
    ];
    const categories = [
        { name: 'ID', value: 'memberId' },
        { name: '이름', value: 'memberName' },
        { name: '연락처', value: 'memberPhone' },
        { name: '상품명', value: 'productName' },
        { name: '당첨일', value: 'createDate' }
    ];
    const sortableColumns = ["", ""];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getProducts()
    renderCategories(categories)


    $("#searchButton").on("click", function () {
        const searchInput = $('#searchInput').val();
        const category = $('#categorySelect').val();

        if (category && !searchInput) {
            alert("검색어를 입력하세요.")
            return
        }
        if (!category && searchInput) {
            alert("카테고리를 선택하세요.")
            return
        }

        if (!category && !searchInput) {
            getProducts()
            return;
        }

        searchProducts(searchInput, category)
    })

    $("#addProductModalButton").on("click", function () {
        // name이 "imageInputType"인 input을 제외한 모든 input 초기화
        $('#addModal').find('input').not('[name="imageInputType"]').val('');

        $('#addModal').modal('show');
    });

    $("#addProductButton").on("click", function () {
        const imageUrl = $("#addProductImageUrl").val();
        const imageFile = $('#addProductImageFile')[0].files[0];
        const name = $("#addProductName").val();
        const probability = $("#addProductProbability").val();
        const price = $("#addProductPrice").val().replace(/,/g, '');;
        const link = $("#addProductLink").val();
        const memo = $("#addProductMemo").val();


        if (!imageUrl && !imageFile) {
            alert("이미지를 입력하세요.")
            return
        }
        // imageUrl 확장자 체크 (JPG, JPEG, PNG만 허용)
        if (imageUrl && !imageUrl.match(/\.(jpg|jpeg|png)$/i)) {
            alert("이미지 URL은 JPG, JPEG, PNG 파일만 허용됩니다.");
            return;
        }

        if (!name) {
            alert("상품명을 입력하세요.")
            return
        }

        if (!price) {
            alert("판매가를 입력하세요.")
            return
        }

        if (probability === "" || isNaN(probability)) {
            alert("확률을 입력하세요.");
            return;
        }

        const parsedProb = parseFloat(probability);
        if (parsedProb < 0 || parsedProb > 100) {
            alert("확률은 0에서 100 사이의 숫자여야 합니다.");
            return;
        }

        if (!link) {
            alert("링크를 입력하세요.")
            return
        }

        if (!memo) {
            alert("메모를 입력하세요.")
            return
        }

        addProduct(imageUrl, imageFile, memo, probability, price, link ,name)
    })

    $("#updateProductButton").on("click",  async function () {
        const productId = $('#updateProductId').val();
        const imageUrl = $("#updateProductImageUrl").val();
        const imageFile = $('#updateProductImageFile')[0].files[0];
        const name = $('#updateProductName').val();
        const memo = $('#updateProductMemo').val();
        const price = $('#updateProductPrice').val().replace(/,/g, '');;
        const link = $('#updateProductLink').val();
        const memberId = $('#updateProductMemberId').val();
        const isUsed = $("#updateProductActive").prop("checked");
        const probability = $("#updateProductProbability").val();

        if (!productId || !memberId) {
            alert("관리자에게 문의하세요");
            return;
        }


        if (!imageUrl && !imageFile) {
            alert("이미지를 입력하세요.")
            return
        }
        // imageUrl 확장자 체크 (JPG, JPEG, PNG만 허용)
        if (imageUrl && !imageUrl.match(/\.(jpg|jpeg|png)$/i)) {
            alert("이미지 URL은 JPG, JPEG, PNG 파일만 허용됩니다.");
            return;
        }

        if (!name) {
            alert("상품명을 입력하세요.")
            return
        }

        if (!price) {
            alert("판매가를 입력하세요.");
            return;
        }

        if (probability === "" || isNaN(probability)) {
            alert("확률을 입력하세요.");
            return;
        }

        const parsedProb = parseFloat(probability);
        if (parsedProb < 0 || parsedProb > 100) {
            alert("확률은 0에서 100 사이의 숫자여야 합니다.");
            return;
        }

        if (!link) {
            alert("링크를 입력하세요.");
            return;
        }

        if (!memo) {
            alert("메모를 입력하세요.");
            return;
        }

        if (isUsed) {
            const result = await isAddProductProbability(productId,probability);

            if (!result) {
                alert("확률의 합이 100을 초과하여 추가할 수 없습니다.");
                return;
            }

        }

        updateProduct(productId, imageUrl, imageFile, memo, price, link, memberId, name, isUsed, probability);
    });

    function searchProducts(searchInput, searchCategory) {
        fetch(`/drawHistory/search?input=${searchInput}&category=${searchCategory}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }
    function getProducts() {
        const page = 0
        const size = 20
        const sort = 'createDate,desc'

        fetch(`/drawHistory/list?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }
    function createTableRow(data, index) {
        'No', 'ID' ,'이름','연락처','주소','상품명','링크', '상품가격', '당첨일자', '상태','지급상태','기타'
        const row = $(`<tr id = ${data.id}>`);
        // 날짜 포맷 변경 (YYYY-MM-DD)
        const formattedCreateDate = formatDate(data.createDate)
        row.append($('<td data-column="Index">').text(index + 1));
        row.append($('<td data-column="memberId">').text(data.memberId));
        row.append($('<td data-column="memberName">').text(data.memberName));
        row.append($('<td data-column="memberPhone">').text(data.memberPhone));
        row.append($('<td data-column="memberAddress">').text(data.memberAddress));
        row.append($('<td data-column="productName">').text(data.productName));
        row.append(
            $('<td data-column="productLink">').append(
                $('<a>')
                    .attr('href', data.productLink)
                    .attr('target', '_blank') // 새 창에서 열기
                    .text(data.productLink)
            )
        );
        row.append($('<td data-column="productPrice">').text(formatNumberWithComma(data.productPrice)));

        row.append($('<td data-column="createDate">').text(formattedCreateDate));
        row.append($('<td data-column="drawStatus">').text(data.drawStatus));
        row.append($('<td data-column="drawStatus">').text(data.drawDeliveryStatus));

        // row.append($('<td data-column="probability">').text(data.probability+"%"));


        if (userRole == '관리자') {
            const actionCell = $('<td style="display: flex; justify-content: center;" data-column="Actions">');
            // 삭제 버튼 추가
            if (data.drawDeliveryStatus == "배송지 입력완료") {
                actionCell.append(successButton(data));
            }

            actionCell.append(deleteButton(data));
            row.append(actionCell);
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }

        return row;
    }
    function updateProduct(productId, imageUrl, imageFile, memo, price, link, memberId, name, isUsed, probability) {
        const formData = new FormData();

        formData.append('id', productId);

        // 이미지 파일이 선택되었으면 추가
        if (imageFile) {
            formData.append('imageFile', imageFile);
        }
        formData.append('name', name);
        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('price', price);
        formData.append('link', link);
        formData.append('memberId', memberId);
        formData.append('probability', probability);

        if (isUsed) {
            formData.append('status', "사용");
        }else {
            formData.append('status', "미사용");
        }


        fetch(`/product`, {
            method: 'PATCH',
            body: formData  // 브라우저가 자동으로 multipart/form-data 설정
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 수정하였습니다.");
                    $('#updateModal').modal('hide');
                    getProducts();
                } else {
                    alert("수정을 실패하였습니다.");
                }
            })
            .catch(error => {
                console.log(error);
            });
    }
    function addProduct(imageUrl, imageFile, memo, probability, price, link, name) {
        const formData = new FormData();
        // 이미지 파일이 선택된 경우에만 파일 데이터를 추가 (둘 중 하나만 선택하도록 설계된 경우)

        if (imageFile) {
            formData.append('imageFile', imageFile);
        }
        formData.append('name', name);
        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('price', price);
        formData.append('probability', probability);
        formData.append('link', link);

        fetch(`/product`, {
            method: 'POST',
            body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 추가하였습니다.");
                    $('#addModal').modal('hide');
                    getProducts();
                } else {
                    alert("추가를 실패하였습니다.");
                }
            })
            .catch(error => {
                console.log(error);
            });
    }
    function deleteButton(data) {
        return $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click', function () {
            if (confirm(`${data.memberName}님의 당첨내역을 삭제하시겠습니까?`)) {
                deleteProduct(data.id)
            }
        })
    }
    function successButton(data) {
        return $('<button>').text('지급완료').addClass('btn btn-primary mx-lg-1 btn-sm').on('click', function () {
            if (confirm(`${data.memberName}님의 경품을 지급완료 하시겠습니까?`)) {
                successProduct(data.id)
            }
        })
    }
    function deleteProduct(id) {
        fetch(`/drawHistory/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 삭제하였습니다.")
                    getProducts()
                } else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }

    function successProduct(id) {
        fetch(`/drawHistory/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 지급완료 처리하였습니다.")
                    getProducts()
                } else {
                    alert("지급완료 처리를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


