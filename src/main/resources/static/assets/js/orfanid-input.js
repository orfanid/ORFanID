$(document).ready(function () {

   // initilizations
    $('#genesequence').trigger('autoresize');
    $('#genesequence').characterCounter();
    $('#ncbi_accession_input').characterCounter();
    $('#input_progressbar').modal();
    $('.tooltipped').tooltip();

    // methods or events
    $("#advanceparameterslink").click(function () {
        $("#advanceparameterssection").toggle(1000);
    });

    $("#ncbi_accession_input").focusout(function(){
        $("#ncbi_accession_input_helper").text("");
        $("#ncbi_accession_input_helper").addClass('valid').removeClass('invalid');
    });

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

    var validated = false;
    $('#input_form').submit(function(event) {
            if(!validated){
                var accessionType = $("input[name=accessionType]:checked").val();
                var ncbi_accession_input = $("#ncbi_accession_input").val();

                // ajax call to controller to validate the values
                $.ajax({
                    url  : "/validate/accessions",
                    type : "GET",
                    async: false,
                    data :{"accessions":ncbi_accession_input, "accessionType":accessionType},
                    success : function(response) {
                        if (response === "Valid") {
                            $('#input_progressbar').modal('open');
                            validated = true;
                            $('form').submit();
                        }else if(response === "Invalid"){
                            $("#ncbi_accession_input_helper").text("No accession");
                        }else{
                            $("#ncbi_accession_input").focus();
                            $("#ncbi_accession_input").addClass('invalid').removeClass('valid');
                            $("#ncbi_accession_input_helper").text(response + " not found in "+ accessionType + " database");
                            $("#ncbi_accession_input_helper").addClass('invalid').removeClass('valid');
                        }
                    },
                    error : function(error) {
                        alert("Error occurred: " + error);
                    }
                });
            }
            return validated;
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

    var example_protein_data_values = {
        "Escherichia coli(562)" : "NP_415100.1,YP_002791247.1,NP_414542.1",
        "Drosophila melanogaster(7227)" : "NP_524859.2",
        "Homo sapiens(9606)" : "NP_001119584.1",
        "Arabidopsis thaliana(3702)" : "NP_187663.1",
    };

    var example_nucliotide_data_values = {
        "Escherichia coli(562)" : "NZ_JAACYZ010000241.1,X86971.1",
        "Drosophila melanogaster(7227)" : "NM_080120.3",
        "Homo sapiens(9606)" : "NM_001126112.2",
        "Arabidopsis thaliana(3702)" : "NM_111887.3",
    };

    // ------------------------------ Example data ------------------------------

    $('.load-example-data').click(function(event){
            var example_name = $(this).prop("name");
            var accession_type = $("input[name=accessionType]:checked").val()

            var genesequence = $('#genesequence');
            var ncbi_accession_input = $('#ncbi_accession_input');

            if($('#example_method').prop('checked')) {
                if(accession_type === "protein"){
                    ncbi_accession_input.val(example_protein_data_values[example_name]);
                }else{
                    ncbi_accession_input.val(example_nucliotide_data_values[example_name]);
                }
                genesequence.val("");
                M.textareaAutoResize(genesequence);
                M.updateTextFields();
                genesequence.prop('disabled', true);
                ncbi_accession_input.prop('disabled', false);
                genesequence.focus(false);
                ncbi_accession_input.focus();
            }else{
                $.get('assets/data/example-' + accession_type + '-' + example_name + '.fasta', function(data) {
                    genesequence.val(data);
                    M.textareaAutoResize($('#genesequence'));
                    M.updateTextFields();
                }, 'text');
                ncbi_accession_input.val("");
                ncbi_accession_input.prop('disabled', true);
                genesequence.prop('disabled', false);
                genesequence.focus();
            }
            $('#organismName').val(example_name);
            return true;
    });

    $("#fastafile").on('change', function () {
           $('#genesequence').trigger('autoresize');
            resizeTextArea($('#genesequence'));
        $('#genesequence').css('overflow-y', 'auto');
    });
});

