var Parchment = Quill.import('parchment');

// Line Height (Giãn dòng)
var lineHeightConfig = {
    scope: Parchment.Scope.INLINE,
    whitelist: ['1.0', '1.2', '1.5', '2.0', '3.0']
};
var LineHeightStyle = new Parchment.Attributor.Style('line-height', 'line-height', lineHeightConfig);
Quill.register(LineHeightStyle, true);

// Font chữ
var Font = Quill.import('formats/font');
Font.whitelist = ['roboto', 'merriweather', 'arial', 'times'];
Quill.register(Font, true);

// Cỡ chữ
var Size = Quill.import('attributors/style/size');
Size.whitelist = ['10px', '13px', '16px', '18px', '24px', '32px'];
Quill.register(Size, true);

var quill = new Quill('#blog-editor', {
    theme: 'snow',
    placeholder: 'Nhập nội dung bài viết...',
    modules: {
        toolbar: {
            container: [
                //  Font & Size
                [{'font': ['roboto', 'merriweather', 'arial', 'times']}],
                [{'size': ['small', false, 'large', 'huge']}],

                // Định dạng văn bản
                ['bold', 'italic', 'underline', 'strike'],
                [{'color': []}, {'background': []}],

                // Paragraph & Line Height
                [{'align': []}],
                [{'line-height': ['1.0', '1.2', '1.5', '2.0', '3.0']}],

                // Heading & Blockquote
                [{'header': 1}, {'header': 2}],
                ['blockquote', 'code-block'],

                // List & Indent
                [{'list': 'ordered'}, {'list': 'bullet'}],
                [{'indent': '-1'}, {'indent': '+1'}],

                // Media
                ['link', 'image', 'video'],

                // Clean format
                ['clean']
            ]
        }
    }
});


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
        const contentHTML = quill.root.innerHTML;
        const contentText = quill.getText().trim();
        if (contentText.length === 0) {
            e.preventDefault();
            alert("Vui lòng nhập nội dung bài viết!");
            return;
        }
        document.getElementById('hidden-content').value = contentHTML;
    });
}