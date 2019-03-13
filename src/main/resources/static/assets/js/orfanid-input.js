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

    $('#findsequence').click(function () {
        $('#input_progressbar').modal('open');
        var ncbi_accession_input = $('#ncbi_accession_input').val(); // 16128551,226524729,16127995
        if (!ncbi_accession_input){
            $('#ncbi_accession_input').removeClass("validate");
        }else{
            var accessionType = $('input[name=accession_type]:checked').val();
            var data = {
                "sequencetype" : accessionType,
                "sequenceids" : ncbi_accession_input
            };

            $.ajax({
                type: "POST",
                contentType: 'application/json',
                async: false,
                dataType: "text",
                url: "/search/sequence",
                data: JSON.stringify(data),
                success: function (result) {
                    console.log(result);
                    $('#genesequence').val(result.toString())
                },
                error: function (error) {
                    $('#genesequence').val(error)
                }
            });
            // $.ajax({
            //     url: 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=' + accessionType + '&id=' + ncbi_accession_input + '&rettype=fasta&retmode=text',
            //     async: false,
            //     dataType: 'json',
            //     success: function (response) {
            //         $('#genesequence').val(response);
            //     }, error: function (error) {
            //         $('#genesequence').val(error.responseText);
            //
            //     }
            // });
        }
        setTimeout(function() {$('#input_progressbar').modal('close')},1000);
    });

    $('#load-ecoli-example-data').click(function () {
        $('#genesequence').load('assets/data/ecoli-example-data.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Escherichia coli(562)');
        // M.textareaAutoResize($('#genesequence'));
        return true;
    });
    $('#load-fly-example-data').click(function () {
        $('#genesequence').load('assets/data/fly-example-data.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Drosophila melanogaster(7227)');
        return true;
    });
    $('#load-human-example-data').click(function () {
        $('#genesequence').load('assets/data/human-example-data.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Homo sapiens(9606)');
        return true;
    });
    $('#load-thaliana-example-data').click(function () {
        $('#genesequence').load('assets/data/thaliana-example-data.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Arabidopsis thaliana(3702)');
        return true;
    });

    $("#fastafile").on('change', function () {
           $('#genesequence').trigger('autoresize');
            resizeTextArea($('#genesequence'));
        $('#genesequence').css('overflow-y', 'auto');
    });
});

$('body').on('change focus', '#genesequence', function () {
    $('#genesequence').css('overflow-y', 'auto');
    $('#genesequence').trigger('autoresize');
    resizeTextArea($(this));
});

function setFileContent(val) {
    var file = document.getElementById("fastafile").files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        var textArea = document.getElementById("genesequence");
        textArea.value = e.target.result;
    };
    reader.readAsText(file);
    M.textareaAutoResize($('#genesequence'));

}

function resizeTextArea($textarea) {

    var hiddenDiv = $('.hiddendiv').first();
    if (!hiddenDiv.length) {
        hiddenDiv = $('<div class="hiddendiv common"></div>');
        $('body').append(hiddenDiv);
    }

    var fontFamily = $textarea.css('font-family');
    var fontSize = $textarea.css('font-size');

    if (fontSize) {
        hiddenDiv.css('font-size', fontSize);
    }
    if (fontFamily) {
        hiddenDiv.css('font-family', fontFamily);
    }

    if ($textarea.attr('wrap') === "off") {
        hiddenDiv.css('overflow-wrap', "normal")
            .css('white-space', "pre");
    }

    hiddenDiv.text($textarea.val() + '\n');
    var content = hiddenDiv.html().replace(/\n/g, '<br>');
    hiddenDiv.html(content);
    console.log($textarea.val());

    // When textarea is hidden, width goes crazy.
    // Approximate with half of window size

    if ($textarea.is(':visible')) {
        hiddenDiv.css('width', $textarea.width());
    }
    else {
        hiddenDiv.css('width', $(window).width() / 2);
    }

    $textarea.css('height', hiddenDiv.height());
    console.log(hiddenDiv.height());
}

