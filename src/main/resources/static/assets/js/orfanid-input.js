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

    $('#submit').click(function () {
        $('#input_progressbar').modal('open');
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

