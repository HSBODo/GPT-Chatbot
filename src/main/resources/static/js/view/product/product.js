$(document).ready(function() {
    let productChart
    const headers = [
        'No', '이미지', '상품명' , '판매가', '할인율(%)','할인가', '링크', '상태', '등록자', '등록일', '기타'
    ];

    const categories = [
        { name: '상품명', value: 'name' },
        { name: '상태', value: 'status' },
        { name: '등록일', value: 'createDate' }
    ];
    const sortableColumns = ["", ""];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getProducts()

    renderCategories(categories)

    // document.getElementById('updateProductActive').addEventListener('change', function () {
    //     const label = document.getElementById('updateProductActiveLabel');
    //     label.textContent = this.checked ? '판매중' : '판매중지';
    // });

    $(document).ready(function () {
        // 추가 모달: 이미지 입력 방식 변경 시, 해당 입력창 보이기/숨기기
        $('input[name="imageInputType"]').on('change', function () {
            if ($(this).val() === 'url') {
                $("#addProductImageUrl").val('');
                $("#addProductImageFile").val('');
                $('#imageUrlGroup').show();
                $('#imageFileGroup').hide();
            } else if ($(this).val() === 'file') {
                $("#addProductImageUrl").val('');
                $("#addProductImageFile").val('');
                $('#imageUrlGroup').hide();
                $('#imageFileGroup').show();
            }
        });

        // 업데이트 모달: 이미지 입력 방식 변경 시, 해당 입력창 보이기/숨기기
        $('input[name="updateImageInputType"]').on('change', function () {
            if ($(this).val() === 'url') {
                $("#updateProductImageUrl").val('');
                $("#updateProductImageFile").val('');
                $('#updateImageUrlGroup').show();
                $('#updateImageFileGroup').hide();
            } else if ($(this).val() === 'file') {
                $("#updateProductImageUrl").val('');
                $("#updateProductImageFile").val('');
                $('#updateImageUrlGroup').hide();
                $('#updateImageFileGroup').show();
            }
        });
    });

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
        $('#addProductMemo').val("");

        $('#addModal').modal('show');
    });

    $("#addProductButton").on("click", function () {
        const getValue = (selector) => $(selector).val().trim();
        const getNumber = (selector) => getValue(selector).replace(/,/g, '');

        const imageUrl = getValue("#addProductImageUrl");
        const imageFile = $('#addProductImageFile')[0].files[0];
        const productRank = getNumber("#addProductRank");
        const name = getValue("#addProductName");
        const price = getNumber("#addProductPrice");
        const discountRateRaw = getNumber("#addDiscountRate");
        const discountPrice = getNumber("#addDiscountPrice");
        const link = getValue("#addProductLink");
        const memo = getValue("#addProductMemo");

        const discountRate = parseFloat(discountRateRaw);

        const validations = [
            { valid: imageUrl || imageFile, message: "이미지를 입력하세요." },
            { valid: imageUrl ? /\.(jpg|jpeg|png)$/i.test(imageUrl) : true, message: "이미지 URL은 JPG, JPEG, PNG 파일만 허용됩니다." },
            { valid: productRank, message: "노출순위를 숫자로 입력해주세요." },
            { valid: name, message: "상품명을 입력하세요." },
            { valid: price, message: "판매원가를 입력하세요." },
            { valid: discountRateRaw, message: "할인율을 입력하세요." },
            { valid: !isNaN(discountRate) && discountRate >= 0 && discountRate <= 100, message: "할인율은 0에서 100 사이여야 합니다." },
            { valid: discountPrice, message: "판매 할인가를 입력하세요." },
            { valid: link, message: "링크를 입력하세요." },
            { valid: memo, message: "메모를 입력하세요." }
        ];

        for (const { valid, message } of validations) {
            if (!valid) {
                alert(message);
                return;
            }
        }

        addProduct(productRank,imageUrl, imageFile, memo, price, discountRateRaw, discountPrice, link, name);
    });

    $("#updateProductButton").on("click", async function () {
        const getValue = (selector) => $(selector).val().trim();
        const getNumber = (selector) => getValue(selector).replace(/,/g, '');

        const productId = getValue('#updateProductId');
        const memberId = getValue('#updateProductMemberId');
        const imageUrl = getValue('#updateProductImageUrl');
        const imageFile = $('#updateProductImageFile')[0].files[0];
        const productRank = getValue('#updateProductRank');
        const name = getValue('#updateProductName');
        const memo = getValue('#updateProductMemo');
        const price = getNumber('#updateProductPrice');
        const discountRateRaw = getNumber('#updateDiscountRate');
        const discountPrice = getNumber('#updateDiscountPrice');
        const link = getValue('#updateProductLink');
        const status = getValue("#updateProductStatus");

        const discountRate = parseFloat(discountRateRaw);

        const validations = [
            { valid: productId && memberId, message: "관리자에게 문의하세요." },
            { valid: imageUrl || imageFile, message: "이미지를 입력하세요." },
            { valid: imageUrl ? /\.(jpg|jpeg|png)$/i.test(imageUrl) : true, message: "이미지 URL은 JPG, JPEG, PNG 파일만 허용됩니다." },
            { valid: productRank, message: "노출순위를 숫자로 입력해주세요." },
            { valid: name, message: "상품명을 입력하세요." },
            { valid: price, message: "판매가를 입력하세요." },
            { valid: !discountRateRaw || (!isNaN(discountRate) && discountRate >= 0 && discountRate <= 100), message: "할인율은 0에서 100 사이여야 합니다." },
            { valid: discountPrice, message: "할인가를 입력하세요." },
            { valid: link, message: "링크를 입력하세요." },
            { valid: memo, message: "메모를 입력하세요." },
        ];

        for (const { valid, message } of validations) {
            if (!valid) {
                alert(message);
                return;
            }
        }

        updateProduct(productId, imageUrl, imageFile, memo, price, discountRate, discountPrice, link, memberId, name, status, productRank);
    });


    function searchProducts(searchInput, searchCategory) {
        fetch(`/product/search?input=${searchInput}&category=${searchCategory}`, {
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
    async function isAddProductProbability(productId,probability) {
        const addData = JSON.stringify({
            id:productId,
            probability:probability,
        });

        try {
            const response = await fetch(`/product/probability`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: addData
            });

            if (!response.ok) {
                alert("확률의 합이 100을 초과하여 추가할 수 없습니다.");
                return false;
            }

            const result = await response.json(); // 서버에서 true/false 받기
            return result; // true or false
        } catch (error) {
            console.error("에러 발생:", error);
            return false;
        }
    }
    function getProducts() {
        const page = 0
        const size = 20
        const sort = 'productRank,asc'

        fetch(`/product/list?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            // getUsedProduct()
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        // ✅ 썸네일 추가 (첫 번째 이미지 사용)
        const thumbnailUrl = (data.images && data.images.length > 0) ? data.images[0] : null;
        const thumbnail = thumbnailUrl
            ? $('<img>').attr('src', thumbnailUrl).css({ width: '60px', height: '60px', objectFit: 'cover' })
            : $('<span>').text('없음');

        const row = $(`<tr id = ${data.id}>`);
        // 날짜 포맷 변경 (YYYY-MM-DD)
        const formattedCreateDate = formatDate(data.createDate)
        const idCell = $('<td data-column="name">').text(data.name)
            .css({cursor: 'pointer', color: 'blue'});
        idCell.on('click', () => openUpdateModal(data));

        row.append($('<td data-column="Index">').text(index + 1));
        row.append($('<td data-column="thumbnail">').append(thumbnail));
        row.append(idCell);
        row.append($('<td data-column="price">').text(formatNumberWithComma(data.price)+"원"));
        row.append($('<td data-column="price">').text(formatNumberWithComma(data.discountRate)+"%"));
        row.append($('<td data-column="price">').text(formatNumberWithComma(data.discountPrice)+"원"));
        row.append(
            $('<td data-column="link">').append(
                $('<a>')
                    .attr('href', data.link)
                    .attr('target', '_blank') // 새 창에서 열기
                    .text(data.link)
            )
        );
        row.append($('<td data-column="status">').text(data.status));
        row.append($('<td data-column="memberName">').text(data.memberName));
        row.append($('<td data-column="createDate">').text(formattedCreateDate));

        if (userRole === '관리자' || id === data.memberId) {
            const actionTd = $('<td data-column="Actions">');
            const actionDiv = $('<div>')
                .css({
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '8px', // 버튼 간 간격
                })
                .append(deleteButton(data));

            actionTd.append(actionDiv);
            row.append(actionTd);
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }

        return row;
    }

    function updateProduct(productId, imageUrl, imageFile, memo, price,  discountRate, discountPrice, link, memberId, name, status, productRank) {
        const formData = new FormData();

        formData.append('id', productId);

        // 이미지 파일이 선택되었으면 추가
        if (imageFile) {
            formData.append('imageFile', imageFile);
        }

        formData.append('productRank', productRank);
        formData.append('name', name);
        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('price', price);
        formData.append('discountRate', discountRate);
        formData.append('discountPrice', discountPrice);
        formData.append('link', link);
        formData.append('memberId', memberId);
        formData.append('status', status);

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

    function addProduct(productRank, imageUrl, imageFile, memo, price, discountRateRaw, discountPrice, link, name) {
        const formData = new FormData();
        // 이미지 파일이 선택된 경우에만 파일 데이터를 추가 (둘 중 하나만 선택하도록 설계된 경우)

        if (imageFile) {
            formData.append('imageFile', imageFile);
        }
        formData.append('productRank', productRank);
        formData.append('name', name);
        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('price', price);
        formData.append('discountRate', discountRateRaw);
        formData.append('discountPrice', discountPrice);
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
            if (confirm(`${data.name}을 삭제하시겠습니까?`)) {
                deleteProduct(data.id)
            }
        })
    }

    function deleteProduct(id) {
        fetch(`/product/${id}`, {
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

    function getUsedProduct() {
        fetch(`/product/used`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderChart(data);
        })
        .catch(error => {
            console.log(error)
        });
    }

    function renderChart(data) {
        $(".chart-container").empty().append(`
            <div class="chart-wrapper">
                <canvas id="productCanvas"></canvas>
                <p>현재 확률</p>
            </div>
        `);

        const ctx = document.getElementById("productCanvas").getContext("2d");

        if (productChart) {
            productChart.destroy();
        }

        // 라벨과 확률 추출
        const labels = data.map(item => item.name);
        const dataValues = data.map(item => item.probability);

        // 확률 총합 계산
        const totalProbability = dataValues.reduce((sum, value) => sum + value, 0);

        // 확률이 100 미만이면 "꽝" 추가
        if (totalProbability < 100) {
            labels.push("꽝");
            dataValues.push(100 - totalProbability);
        }

        // 색상 자동 생성
        const backgroundColors = labels.map((label, i) =>
            label === "꽝" ? "#d3d3d3" : `hsl(${(i * 360) / labels.length}, 70%, 60%)`
        );

        productChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: '상품 확률',
                        data: dataValues,
                        backgroundColor: backgroundColors,
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return `${context.raw}%`;
                            }
                        }
                    }
                }
            }
        });
    }

    function openUpdateModal(data) {
        // 기존 이미지 데이터에 따라 이미지 입력 방식 결정
        if (data.images && (Array.isArray(data.images) ? data.images.length > 0 : data.images !== '')) {
            // data.images가 배열인 경우 첫 번째 값을 사용하고, 문자열인 경우 그대로 사용
            const imageUrl = Array.isArray(data.images) ? data.images[0] : data.images;
            $("input[name='updateImageInputType'][value='url']").prop('checked', true);
            $('#updateImageUrlGroup').show();
            $('#updateImageFileGroup').hide();
            $("#updateProductImageUrl").val(imageUrl);
        } else {
            // 기존 이미지가 없으면 파일 입력 방식을 기본으로 선택
            $("input[name='updateImageInputType'][value='file']").prop('checked', true);
            $('#updateImageUrlGroup').hide();
            $('#updateImageFileGroup').show();
            $("#updateProductImageFile").val('');
        }

        // 나머지 데이터 채우기
        $('#updateProductId').val(data.id);
        $('#updateProductRank').val(data.productRank);
        $('#updateProductName').val(data.name);
        $('#updateProductMemo').val(data.memo);
        $('#updateProductProbability').val(data.probability);
        $('#updateProductPrice').val(formatNumberWithComma(data.price));
        $('#updateDiscountRate').val(formatNumberWithComma(data.discountRate));
        $('#updateDiscountPrice').val(formatNumberWithComma(data.discountPrice));
        $('#updateProductLink').val(data.link);
        $('#updateProductMemberId').val(data.memberId);
        $('#updateProductActive').prop('checked', data.status === '판매중');
        $('#updateProductStatus').val(data.status); // 예: "판매중"
        // 업데이트 모달 열기
        $('#updateModal').modal('show');
    }

})


