// fetch instructions as a markdown file, compile it, and write the compiled instructions onto the page with a corresponding table of contents
$(document).ready(function() {
    $('.scrollspy').scrollSpy();
    fetch("assets/md/instruction.md") // fetch instructions as a markdown file
    .then(resp => resp.text()) // extract the text from the response
    // compile it, and write the compiled instructions onto the page with a corresponding table of contents
    .then(md => {
        // use "marked" library to convert markdown string into an html string
        $('#markdown').html(marked(md));
        $('#markdown img').each((index, el) => {

        });
        let toc = $("#table-of-contents");
        // when markdown compiles, headers are given an id. We add anything with an id to the table of contents as a hyperlink
        $('#markdown *').each((index, el) => {
            if (el.id) {
                toc.append(`<li><a href="#${el.id}">${el.innerText}</a></li>`);
            }
        });
    });
});