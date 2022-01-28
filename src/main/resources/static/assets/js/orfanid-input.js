/* 
 * Global variable containing a list of organism names and links to an image of them. This is used for autocompletion. It contains entries in the following format:
 *      "Acanthochromis polyacanthus(80966)" : "http://www.ensembl.org/i/species/Acanthochromis_polyacanthus.png"
 * This variable is global because it is used repeatedly by the accession number input and the organism name input. When the DOM is ready, organismData is filled using an API call.
 */
var organismData = {};

// Global timeout object which sends an ajax request to NCBI when a user does not type for a certain amount of time, using a timer prevents requests from being made excessively when the user types accessions by hand.
var accessionTimer;

// Stores a properly formated version of whatever sequence the user inputs manually, or "null" if thier input cannot be parsed.
var reformatted = null;

// Callback function for when the accessionTimer ends. This function validates the accession number and informs the user accordingly.
function doneTypingAccession() {
    var accessionType = $("input[name=accessionType]:checked").val(); // String - either protein or nucleotide
    var accessions = $("#ncbi_accession_input").val();

    // An ajax GET request is sent to NCBI to retrieve the fasta file for the accession number.
    $.ajax({
        url: `https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=${encodeURIComponent(accessionType)}&id=${encodeURIComponent(accessions)}&rettype=fasta&retmode=text`,
        type: "GET",
        async: true,
        // If the request succeeds, 
        success: function(response) {
            // Get the portion of text inside of the brackets
            let organismName = response.slice(response.indexOf("[") + 1, response.indexOf("]"));
            
            // Trim away any words that contain numbers. This is useful because the species name is sometimes accompanied by an ID from another database.
            let name = organismName.split(" ").filter((word) => {
                return word.search(/[0-9]/) == -1;
            }).join(" ");

            // Search the organismData object for an organism containing the organism name above.
            Object.entries(organismData).find(([key, value]) => {
                if (key.search(name) != -1) {
                    $("#organismName").val(key);
                    return true;
                }
            });
            
            $("#submit").prop("disabled", false); // Enable form submission.
            $("#genesequence").val(response); // autofill gene sequence with NCBI's FASTA response.
            $("#ncbi_accession_input_helper").text(""); // Remove any error messages under the accession number input.
            M.textareaAutoResize($("#genesequence"));
            M.updateTextFields();
        },
        // If the request fails, we assume that the accession number is not valid.
        error: function(err) {
            $("#submit").prop("disabled", true); // disable form submission
            $("#ncbi_accession_input").addClass('invalid').removeClass('valid'); // Add red line under accession input
            $("#ncbi_accession_input_helper").text(`"${accessions}" not found in the ${accessionType} database`); // Add error message under accession input.
            //$("#ncbi_accession_input_helper").addClass('invalid').removeClass('valid'); // 
        }
    });
}

// Listener function called whenever accession number or accession type is manually changed. It starts/restarts the timer, stopping it if the accession field is blank.
function accessionChangeHandler() {
    clearTimeout(accessionTimer);
    let accession_value = $("#ncbi_accession_input").val();
    if (accession_value == "") {
        $("#genesequence").prop('disabled', false); // User can enter the gene sequence manually only if the accession field is blank.
        $("#ncbi_accession_input_helper").text(""); // Clear existing error messages.
    } else {
        // Ensure that the input length does not exceed the maximum of 100 characters.
        if (accession_value.length > 100) {
            // Accession input is too long.
            $("#submit").prop("disabled", true);
            $("#ncbi_accession_input").addClass('invalid').removeClass('valid');
            $("#ncbi_accession_input_helper").text(`Please limit to 100 characters or less.`);
            $("#ncbi_accession_input_helper").addClass('invalid').removeClass('valid');
        } else {
            // If the input passess the character count check, set a timer to validate it by an ajax request.
            $("#ncbi_accession_input_helper").text(`Validating...`);
            $("#genesequence").prop('disabled', true);
            accessionTimer = setTimeout(doneTypingAccession, 1000);
        }
    }
}

/* 
 * Validates a fasta sequence string with
 * params
 *   fasta: the string containing a putative single fasta sequence
 *   type: should be either "protein" or "nucleotide". if it does not match one of these, it will not use type-specific regex for validation.
 * if the fasta is valid, a cleaned-up version of the fasta file os returned
 * if invalid, null is returned.
 */
function validateFasta(fasta, type) {
    fasta = fasta.trim(); // take away leading and trailing whitespace
    if (fasta[0] != ">" && fasta[0] != ";") return null; // Make sure that the very first character is a > or ;
    let reformatted = ("\n" + fasta).split(/\n>|\n;/g) // splits sequences from a single string into a list, removing the leading ; or > from them.
    .map(sequence => sequence.trim()) // trim each element in this list
    .filter(sequence => sequence.length != 0); // remove any empty list elements

    // remove trailing "*" character
    reformatted = reformatted.map(sequence => {
        if (sequence[sequence.length - 1] === "*") {
            return sequence.slice(0, sequence.length - 1);
        } else {
            return sequence;
        }
    });

    let invalid = false; // flag that will be switched if any of the sequences are invalid.

    // check validity and convert gene data to uppercase (does not convert the comment portion to uppercase)
    reformatted = reformatted.map(sequence => {
        let comment = sequence.slice(0, sequence.indexOf("\n")); // header of sequence
        let data = sequence.slice(sequence.indexOf("\n") + 1); // body of sequences
        let invalidRegex; // if this regular expression is found in the sequence, the sequence must be invalid.

        // which characters we allow depends on the type of sequence.
        switch (type) {
            case "nucleotide": // two cases are caught to protect against a common mis-spelling of nucleotide
            case "nucliotide": // only A, T, C, G are allowed for nucleotides
                invalidRegex = /\n\n|(?!(A|T|C|G|\n))/gmi;
                break;
            case "protein":
                invalidRegex = /\n\n|(?!(A|C|D|E|F|G|H|I|K|L|M|N|P|Q|R|S|T|V|W|Y|\n))/gmi;
                break;
            default:
                invalidRegex = /(\n\n|*|;|>)/gm;
                break;
        }
        let findInvalid = data.search(invalidRegex); // sets findInvalid to -1 if the regex is not found
        if (findInvalid > -1 && findInvalid < data.length) invalid = true; // Mark as invalid if any invalid characters are found in the sequence portion
        return comment + "\n" + data.toUpperCase(); // convert sequence body to uppercase, combine it with the header, and return it
    });
    if (invalid) return null;
    else return ">" + reformatted.join("\n\n>"); // recombine the list of sequences into a single re-formatted string.
}

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

    // $("#ncbi_accession_input").focusout(function(){
    //     $("#ncbi_accession_input_helper").text("");
    //     $("#ncbi_accession_input_helper").addClass('valid').removeClass('invalid');
    // });

    // populate the global organismData variable with names and image links
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
        // Form data is validated immediately, so re-validating it on form submission is not necessary.
        $('#input_progressbar').modal('open');
        $('form').submit();
        return validated;
    });

    // Validate manually input gene sequences, and disable the ability to input a gene sequence if accessions are provided and vice versa
    $("#genesequence").keyup(function () {
        var genesequence = $(this).val();
        if (genesequence !== "") {
            // validate that gene sequence is in fasta format and not unreasonably large.
            $("#ncbi_accession_input").prop('disabled', true);
            if (genesequence.length <= 10000) {
                var accessionType = $("input[name=accessionType]:checked").val(); // String - either protein or nucleotide
                reformatted = validateFasta(genesequence, accessionType); // TODO: there could be a button that reformats the fasta file using this function.
                //console.log(reformatted);
                if (reformatted != null) {
                    // Valid FASTA format
                    $("#submit").prop("disabled", false);
                    $("#genesequence_helper").text("");
                    $("#genesequence").addClass('valid').removeClass('invalid');
                } else {
                    // Invalid FASTA format
                    $("#submit").prop("disabled", true);
                    $("#genesequence_helper").text("Invalid format.");
                    $("#genesequence").addClass('invalid').removeClass('valid');
                }
            } else {
                // Input too large
                $("#submit").prop("disabled", true);
                $("#genesequence_helper").text("Please limit file uploads to 10KB or less.");
                $("#genesequence").addClass('invalid').removeClass('valid');
            }
        } else {
            $("#ncbi_accession_input").prop('disabled', false);
        }
    });

    // Begin to check the accession number whenever the accession type or the accession number field are manually changed.
    $("input[name=accessionType]").click(accessionChangeHandler);
    $("#ncbi_accession_input").keyup(accessionChangeHandler);

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
        // resizeTextArea($('#genesequence')); // I commented this out because it threw an error. Please review this and uncomment if important.
        $('#genesequence').css('overflow-y', 'auto');

        // ensure that file size is not too large.
        // 10000 Byte UTF-8 file = 10000 characters.
        if ($("#fastafile").prop('files')[0] && $("#fastafile").prop('files')[0].size > 10000) {
            // File is too large.
            $("#submit").prop("disabled", true);
            $("#fastafile_helper").text("Please limit file uploads to 10KB or less.");
            $("#fastaFileName").addClass('invalid').removeClass('valid');
        } else {
            // File is small enough to process.
            $("#submit").prop("disabled", false);
            $("#fastafile_helper").text("");
            $("#fastaFileName").addClass('valid').removeClass('invalid');
        }
    });
});