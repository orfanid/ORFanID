$(document).ready(function () {

    $("#advanceparameterslink").click(function () {
        $("#advanceparameterssection").toggle(1000);
    });
    $('#genesequence').trigger('autoresize');
    $('#input_progressbar').modal();
    $('.tooltipped').tooltip();

    var organismData = {};
    $.getJSON('assets/data/organism_list.json', function(data) {
        $.each( data, function( key, val ) {
            organismData[key] =  val ;
        });
    }).done(function() {
        var organismElement = $('#organismName');
        organismElement.autocomplete({
            data: organismData
        });
    });

    // Disable the ability to input a gene sequence if accessions are provided and vice versa
    $("#genesequence").keyup(function () {
        var genesequence = $(this).val();
        if (genesequence !== "") {
            $("#ncbi_accession_input").prop('disabled', true);
        } else {
            $("#ncbi_accession_input").prop('disabled', false);
        }
    });
    $("#ncbi_accession_input").keyup(function () {
        var accessions = $(this).val();
        if (accessions !== "") {
            $("#genesequence").prop('disabled', true);
        } else {
            $("#genesequence").prop('disabled', false);
        }
    });

    $('#load-ecoli-example-data').click(function () {
        $.get('assets/data/ecoli-example-data.fasta', function(data) {
            $('#genesequence').val(data);
            M.textareaAutoResize($('#genesequence'));
            M.updateTextFields();
            $('#organismName').val('Escherichia coli(562)');
        }, 'text');
        return true;
    });
    $('#load-fly-example-data').click(function () {
        $.get('assets/data/fly-example-data.fasta', function(data) {
            $('#genesequence').val(data);
            M.textareaAutoResize($('#genesequence'));
            M.updateTextFields();
            $('#organismName').val('Drosophila melanogaster(7227)');
        }, 'text');
        return true;
    });
    $('#load-human-example-data').click(function () {
        $.get('assets/data/human-example-data.fasta', function(data) {
            $('#genesequence').val(data);
            M.textareaAutoResize($('#genesequence'));
            M.updateTextFields();
            $('#organismName').val('Homo sapiens(9606)');
        }, 'text');
        return true;
    });
    $('#load-thaliana-example-data').click(function () {
        $.get('assets/data/thaliana-example-data.fasta', function(data) {
            $('#genesequence').val(data);
            M.textareaAutoResize($('#genesequence'));
            M.updateTextFields();
            $('#organismName').val('Arabidopsis thaliana(3702)');
        }, 'text');
        return true;
    });

    $("#fastafile").on('change', function () {
           $('#genesequence').trigger('autoresize');
            resizeTextArea($('#genesequence'));
        $('#genesequence').css('overflow-y', 'auto');
    });
});

// $('#genesequence').on('input',function(e){
//     resizeTextArea($(this));
// });

// $('body').on('change', '#genesequence', function () {
//     console.log("changed");
//     // M.textareaAutoResize(document.querySelector('#genesequence'))
//     // $('#genesequence').css('overflow-y', 'auto');
//     // $('#genesequence').trigger('autoresize');
//     // resizeTextArea($(this));
//     // setTimeout(function(){ $('.input-field label').addClass('active'); }, 1);
// });

// function setFileContent(val) {
//     var file = document.getElementById("fastafile").files[0];
//     var reader = new FileReader();
//     reader.onload = function (e) {
//         var textArea = document.getElementById("genesequence");
//         textArea.value = e.target.result;
//     };
//     reader.readAsText(file);
//     M.textareaAutoResize($('#genesequence'));
//
// }
//
// function resizeTextArea($textarea) {
//
//     var hiddenDiv = $('.hiddendiv').first();
//     if (!hiddenDiv.length) {
//         hiddenDiv = $('<div class="hiddendiv common"></div>');
//         $('body').append(hiddenDiv);
//     }
//
//     var fontFamily = $textarea.css('font-family');
//     var fontSize = $textarea.css('font-size');
//
//     if (fontSize) {
//         hiddenDiv.css('font-size', fontSize);
//     }
//     if (fontFamily) {
//         hiddenDiv.css('font-family', fontFamily);
//     }
//
//     if ($textarea.attr('wrap') === "off") {
//         hiddenDiv.css('overflow-wrap', "normal")
//             .css('white-space', "pre");
//     }
//
//     hiddenDiv.text($textarea.val() + '\n');
//     var content = hiddenDiv.html().replace(/\n/g, '<br>');
//     hiddenDiv.html(content);
//     console.log($textarea.val());
//
//     // When textarea is hidden, width goes crazy.
//     // Approximate with half of window size
//
//     if ($textarea.is(':visible')) {
//         hiddenDiv.css('width', $textarea.width());
//     }
//     else {
//         hiddenDiv.css('width', $(window).width() / 2);
//     }
//
//     $textarea.css('height', hiddenDiv.height());
//     console.log(hiddenDiv.height());
// }

