const cardContainer = document.getElementById('cardContainer');
const loading = document.getElementById('loading');
let page = 0;          // 페이지 번호 (0부터 시작)
let isLoading = false; // 로딩 중 상태
let hasMore = true;    // 더 불러올 데이터 여부
let itemIndex = 1;     // 순번 (1부터 시작)
// 초기 정렬 순서 (내림차순)
let saleDateSortOrder = 'desc';
let createDateSortOrder = 'desc';
// 📍 URL에서 상품 ID 가져오기
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('id');  // 예: ?id=123

function getSaleDateColor(saleDate) {
    if (!saleDate) return '#000'; // 날짜가 없으면 기본 검정색 반환

    const sale = new Date(saleDate);
    // todayKST 문자열("YYYY-MM-DD")를 분해하여 KST 기준의 Date 객체 생성
    const [year, month, day] = todayKST.split('-');
    const today = new Date(year, month - 1, day); // 월은 0부터 시작

    // sale의 시간 요소 제거 (날짜만 비교)
    sale.setHours(0, 0, 0, 0);

    const diffDays = (sale - today) / (1000 * 60 * 60 * 24);

    if (diffDays < 0) {
        return '#9E9E9E'; // 과거: 회색
    } else if (diffDays <= 1) {
        return '#ff0000'; // 오늘 또는 하루전: 빨간색
    } else if (diffDays <= 7) {
        return '#ff7e22'; // 7일 이내: 주황색
    } else if (diffDays <= 14) {
        return '#4caf50'; // 14일 이내: 초록
    } else {
        return '#2139dc'; // 그 외: 파란
    }
}

$('#sortSaleDateButton').html('매각기일순 ▼');
$('#sortCreateDateButton').html('등록일순 ▼');

$('#sortCreateDateButton').on('click', function () {
    // 정렬 순서 토글 및 버튼 색, 화살표 업데이트
    if (createDateSortOrder === 'desc') {
        createDateSortOrder = 'asc';
        $(this)

            .html('등록일순 ▲'); // 오름차순: ▲ 표시
    } else {
        createDateSortOrder = 'desc';
        $(this)

            .html('등록일순 ▼'); // 내림차순: ▼ 표시
    }

    // 기존 데이터를 초기화
    $(cardContainer).empty();
    page = 0;
    hasMore = true;
    itemIndex = 1;

    // saleDate 기준 정렬로 데이터 불러오기
    fetchData("createDate", createDateSortOrder);
});
$('#sortSaleDateButton').on('click', function () {
    // 정렬 순서 토글 및 버튼 색, 화살표 업데이트
    if (saleDateSortOrder === 'desc') {
        saleDateSortOrder = 'asc';
        $(this)

            .html('매각기일 ▲'); // 오름차순: ▲ 표시
    } else {
        saleDateSortOrder = 'desc';
        $(this)

            .html('매각기일 ▼'); // 내림차순: ▼ 표시
    }

    // 기존 데이터를 초기화
    $(cardContainer).empty();
    page = 0;
    hasMore = true;
    itemIndex = 1;

    // saleDate 기준 정렬로 데이터 불러오기
    fetchData("saleDate", saleDateSortOrder);
});

// 🚩 카드 렌더링 함수 (특정 상품은 '선택 매물' 표시)
function renderCard(item, isSelected = false) {
    const card = document.createElement('div');
    card.className = 'card shadow-lg rounded-lg mt-5';
    card.setAttribute('data-product-id', item.id ?? '');
    card.style.borderWidth = '3px';  // 두께 조절
    card.style.borderStyle = 'solid';
    card.style.borderColor = '#ff6600';  // 주황색 테두리


    // 🏷️ 순번 또는 '선택 매물' 표시
    const label = isSelected ? '선택 매물' : `순번: ${itemIndex++}`;
    const imageUrl = item.images?.[0] ?? '/loginLogo.png';
    const isValidUrl = imageUrl && (imageUrl.startsWith('http://') || imageUrl.startsWith('https://'));

    // todayKST는 "YYYY-MM-DD" 형식 문자열로 이미 정의되어 있다고 가정합니다.
    // saleDate를 Date 객체로 만들고, 시간 요소를 제거합니다.
    const saleDateObj = new Date(item.saleDate);
    saleDateObj.setHours(0, 0, 0, 0);

    // todayKST 문자열을 분해하여 KST 기준의 Date 객체 생성
    const [year, month, day] = todayKST.split('-');
    const todayKSTDate = new Date(year, month - 1, day);

    // 두 날짜의 차이를 일(day) 단위로 계산합니다.
    const diffDays = Math.round((todayKSTDate - saleDateObj) / (1000 * 60 * 60 * 24));
    let diffText;
    if (diffDays > 0) {
        diffText = `(${diffDays}일전)`;
    } else if (diffDays < 0) {
        diffText = `(${Math.abs(diffDays)}일후)`;
    } else {
        diffText = '(오늘)';
    }
    const saleDateDisplay = item.saleDate ? `${item.saleDate} ${diffText}` : '';

    card.innerHTML = `
        <img src="${isValidUrl ? imageUrl : '/loginLogo.png'}" alt="image" class="card-img-top"
             onerror="this.onerror=null;this.src='/loginLogo.png';" referrerpolicy="no-referrer">
<!--        <div class="card-header"><strong>${label}</strong></div>-->
        <div class="card-body">
           <div class="info-item" style="color: ${getSaleDateColor(item.saleDate)}; font-weight: bold;">
                <strong>매각 기일:</strong> ${saleDateDisplay}
            </div>
            <div class="info-item"><strong>감정가:</strong> ${item.price ? formatNumberWithComma(item.price) + '원' : ''}</div>
            <div class="info-item"><strong>최저가:</strong> ${item.minPrice ? formatNumberWithComma(item.minPrice) + '원' : ''}</div>
            <br>
            <div class="info-item"><strong>타경번호:</strong> ${item.no ?? ''}</div>
            <div class="info-item"><strong>물건종류:</strong> ${item.category ?? ''}</div>
            <div class="info-item"><strong>소재지:</strong> ${item.location ?? ''}</div>
            <br>
            <div class="info-item"><strong>현시세:</strong> ${item.currentPrice ? formatNumberWithComma(item.currentPrice) + '원' : ''}</div>
            <div class="info-item" style="color:#dc3545"><strong>예상가:</strong> ${item.expectedPrice ? formatNumberWithComma(item.expectedPrice) + '원' : ''}</div>
            <br>
            <div class="info-item"><strong>특이사항:</strong> ${item.description ?? ''}</div>
            <div class="info-item"><strong>링크:</strong> <a href="${item.link ?? '#'}" target="_blank">상세 보기</a></div>
                 <br>
            <div class="info-item"><strong>권리분석사:</strong> ${item.managerName ?? ''}</div>
            <div class="info-item"><strong>상담전화:</strong> ${item.managerPhone ?? ''}</div>
        </div>
    `;

    // 🚀 선택 매물은 상단에, 일반 매물은 하단에 추가
    if (isSelected) {
        cardContainer.prepend(card);  // 선택 매물은 상단
    } else {
        cardContainer.appendChild(card);  // 일반 매물은 하단
    }

    return card;
}

// 🚩 특정 상품 ID로 조회 (선택 매물 표시)
async function fetchProductById(productId) {
    try {
        const response = await fetch(`/product/${productId}`);
        if (!response.ok) throw new Error('상품을 찾을 수 없습니다.');
        const product = await response.json();

        renderCard(product, true);  // 🚀 선택 매물로 렌더링
    } catch (error) {
        console.error('❌ 선택 매물 조회 실패:', error);
        alert('해당 상품을 찾을 수 없습니다.');
    }
}

async function renderSearchProducts(items) {
    try {
        if (items.length === 0) throw new Error('상품을 찾을 수 없습니다.');
        $(cardContainer).empty();
        itemIndex = 1;
        items.forEach(item => renderCard(item, false));
    } catch (error) {
        console.error('❌ 선택 매물 조회 실패:', error);
        alert('해당 상품을 찾을 수 없습니다.');
    }
}

// 🚩 일반 데이터 불러오기 (무한 스크롤)
// fetchData 함수에 기본 파라미터를 지정하여, 인자를 전달하지 않으면 "createDate","desc"가 사용됩니다.
async function fetchData(sort = "createDate", direction = "desc") {
    if (isLoading || !hasMore) return;
    isLoading = true;
    loading.style.display = 'block';

    try {
        const response = await fetch(`/product/previous?page=${page}&size=10&sort=${sort}&direction=${direction}`);
        const data = await response.json();
        if (data.content && data.content.length > 0) {
            data.content.forEach(item => renderCard(item));
            page++;  // 다음 페이지로 이동
        }
        if (data.last) {
            hasMore = false;
            loading.innerText = '✅ 모든 데이터를 불러왔습니다.';
        }
    } catch (error) {
        console.error('❌ 데이터 불러오기 실패:', error);
    } finally {
        isLoading = false;
        loading.style.display = 'none';
        observeLastCard(sort, direction); // 마지막 카드 감지
        // 추가: 만약 카드 수가 적어 스크롤이 발생하지 않는 경우 자동 추가 호출
        if (document.body.scrollHeight <= window.innerHeight && hasMore) {
            fetchData(sort, direction);
        }
    }
}

// 🚩 마지막 상품 감지 (IntersectionObserver 사용)
function observeLastCard(sort, direction) {
    const cards = document.querySelectorAll('.card');
    const lastCard = cards[cards.length - 1];

    if (!lastCard) return;

    const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && hasMore) {
            observer.disconnect(); // 중복 호출 방지
            fetchData(sort, direction);
        }
    }, { threshold: 0.5 }); // threshold를 0.5로 변경 (화면의 50%만 보여도 동작)

    observer.observe(lastCard);
}


$("#searchButton").on("click",function () {
    const searchInput = $('#searchInput').val();
    const category = $('#categorySelect').val();
    if (category && !searchInput) {
        alert("검색어를 입력하세요.");
        return;
    }
    if (!category && searchInput) {
        alert("카테고리를 선택하세요.");
        return;
    }

    if (!category && !searchInput) {
        // 기존 카드, 페이지, 상태 등을 초기화
        $(cardContainer).empty();
        page = 0;
        hasMore = true;
        itemIndex = 1;
        fetchData();  // 기본값 "createDate", "desc"가 사용됩니다.
        return;
    }

    searchProducts(searchInput, category);
});

function searchProducts(searchInput, searchCategory) {
    fetch(`/product/search?input=${searchInput}&category=${searchCategory}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            renderSearchProducts(data);
        })
        .catch(error => {
            console.log(error);
        });
}
// 🚩 실행 로직
if (productId) {
    console.log(`🔑 특정 상품 ID 감지됨: ${productId}`);
    fetchProductById(productId);  // 🚀 선택 매물로 상단에 표시
}

fetchData();  // 인자를 전달하지 않으면 기본 "createDate", "desc" 사용

$(document).ready(function(){
    renderCategoriesToRealEstate();
});
