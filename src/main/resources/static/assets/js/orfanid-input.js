$(document).ready(function () {

    $("#advanceparameterslink").click(function () {
        $("#advanceparameterssection").toggle(1000);
    });
    $('#genesequence').trigger('autoresize');
    $('.modal').modal();
    $('.tooltipped').tooltip();

    var organismData = {};
    $.getJSON('assets/data/organism_list_test.json', function(data) {
        $.each( data, function( key, val ) {
            organismData[key] = "'" + val + "'";
        });
    }).done(function() {
        var organismElement = $('#organismName');
        console.log(organismData);
        organismElement.autocomplete({
            data: organismData
        });
    });

    $('#findsequence').click(function () {
        var ncbi_accession_input = $('#ncbi_accession_input').val(); // 16128551,226524729,16127995
        if (!ncbi_accession_input){
            $('#ncbi_accession_input').removeClass("validate");
        }else{
            $.ajax({
                url: 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id='+ncbi_accession_input+'&rettype=fasta&retmode=text',
                async: false,
                dataType: 'json',
                success: function (response) {
                    $('#genesequence').val(response);
                }, error: function (error) {
                    $('#genesequence').val(error.responseText);
                    $('.modal').close();
                }
            });

        }
        $('.modal').close();
    });

    $('#load-example-data').click(function () {
        $('#genesequence').load('assets/data/Ecoli_511145.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Escherichia coli(562)');
        $.ajax({
            url: 'assets/data/TaxData.json',
            async: false,
            dataType: 'json',
            success: function (response) {
                var selectedOrganism = $('#organismName').val();
                var regularExpr = /\((.*)\)/;

                var selectedOrganismTaxID = selectedOrganism.match(regularExpr)[1];
                $.each(response, function (key, val) {
                    var options = "";
                    if (val.NCBITaxID == selectedOrganismTaxID) {
                        $('select').empty().html(' ');
                        $.each(val.Taxonomy, function (key, val) {
                            var value = val.substr(9, val.length);
                            $('select').append($("<option></option>").attr("value", value).text(value));
                        });
                        // re-initialize (update)
                        // $('select').material_select();
                        // $('select').formSelect();
                    }
                });
            },
            error: function (error) {
                alert(error);
            }
        });
        $(function () {
            Materialize.updateTextFields();
        });
        return true;
    });

    $("#fastafile").on('change', function () {

        //    $('#genesequence').trigger('autoresize');
        //     resizeTextArea($('#genesequence'));
        $('#genesequence').css('overflow-y', 'auto');
    });
});

$('body').on('change focus', '#genesequence', function () {
    $('#genesequence').css('overflow-y', 'auto');
    $('#genesequence').trigger('autoresize');
    resizeTextArea($(this));
});

//Optional but keep for future
function setFileContent(val) {
    var file = document.getElementById("fastafile").files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        var textArea = document.getElementById("genesequence");
        textArea.value = e.target.result;
    };
    reader.readAsText(file);

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

