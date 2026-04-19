// Coi trước ảnh
const thumnailInput = document.getElementById('thumbnail-input');
const thumbnailPreview = document.getElementById('thumbnail-preview');
const uploadLabel = document.querySelector('.upload-label');

if (thumnailInput) {
    thumnailInput.addEventListener('change', e => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (event) {
                thumbnailPreview.src = event.target.result;
                thumbnailPreview.style.display = 'block';
                uploadLabel.textContent = file.name;
            }
            reader.readAsDataURL(file);
        } else {
            thumbnailPreview.style.display = 'none';
            uploadLabel.textContent = 'Chọn ảnh..';
        }
    });
}

// Xử lý khi nhấn submit
const blogForm = document.getElementById('blog-form');
if (blogForm) {
    blogForm.addEventListener('submit', function (e) {
        const contentData = CKEDITOR.instances['blog-editor'].getData();
        if (contentData.trim() === '') {
            e.preventDefault();
            alert("Vui lòng nhập nội dung bài viết!");
        }
    });
}