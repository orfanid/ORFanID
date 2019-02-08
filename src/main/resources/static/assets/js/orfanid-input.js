$(document).ready(function () {

    $("#advanceparameterslink").click(function () {
        $("#advanceparameterssection").toggle(1000);
    });
    $('#genesequence').trigger('autoresize');
    $('.modal').modal();
    $('select').material_select();

    var organismElement = $('#organismName');
    organismElement.autocomplete({
        data: {
            // requested by users
            "Escherichia coli(562)": 'https://pax-db.org/images/species/511145.jpg',
            "Oryza sativa(4530)": 'https://upload.wikimedia.org/wikipedia/commons/1/19/Rice_Plants_%28IRRI%29.jpg',
            "Zea mays(4577)": 'https://upload.wikimedia.org/wikipedia/commons/e/e3/Zea_mays_-_Köhler–s_Medizinal-Pflanzen-283.jpg',
            "Tribolium(7069)": 'https://upload.wikimedia.org/wikipedia/commons/3/37/Tribolium_castaneum87-300.jpg',
            "Litopenaeus vannamei(6689)": 'https://en.wikipedia.org/wiki/Whiteleg_shrimp#/media/File:Litopenaeus_vannamei_specimen.jpg',
            "Daphnia(6668)": 'https://en.wikipedia.org/wiki/Daphnia#/media/File:Daphnia_pulex.png',
            "Arabidopsis thaliana(3702)": 'https://en.wikipedia.org/wiki/Arabidopsis_thaliana#/media/File:Arabidopsis_thaliana.jpg',
            "Bemisia tabaci(7038)": 'https://upload.wikimedia.org/wikipedia/commons/a/a7/Silverleaf_whitefly.jpg',
            "Leishmania(38568)": 'https://en.wikipedia.org/wiki/Leishmania#/media/File:Leishmania_donovani_01.png',
            "Bombyx mori(7091)": 'https://en.wikipedia.org/wiki/Bombyx_mori#/media/File:Pairedmoths.jpg',
            "Oryza punctata(4537)": 'https://en.wikipedia.org/wiki/Oryza',
    // from ensembl
    "Acanthochromis polyacanthus(80966)":'http://www.ensembl.org/i/species/Acanthochromis_polyacanthus.png',
    "Ailuropoda melanoleuca(9646)":'http://www.ensembl.org/i/species/Ailuropoda_melanoleuca.png',
    "Amphilophus citrinellus(61819)":'http://www.ensembl.org/i/species/Amphilophus_citrinellus.png',
    "Amphiprion ocellaris(80972)":'http://www.ensembl.org/i/species/Amphiprion_ocellaris.png',
    "Amphiprion percula(161767)":'http://www.ensembl.org/i/species/Amphiprion_percula.png',
    "Anabas testudineus(64144)":'http://www.ensembl.org/i/species/Anabas_testudineus.png',
    "Anas platyrhynchos(8839)":'http://www.ensembl.org/i/species/Anas_platyrhynchos.png',
    "Anolis carolinensis(28377)":'http://www.ensembl.org/i/species/Anolis_carolinensis.png',
    "Aotus nancymaae(37293)":'http://www.ensembl.org/i/species/Aotus_nancymaae.png',
    "Astatotilapia calliptera(8154)":'http://www.ensembl.org/i/species/Astatotilapia_calliptera.png',
    "Astyanax mexicanus(7994)":'http://www.ensembl.org/i/species/Astyanax_mexicanus.png',
    "Bos taurus(9913)":'http://www.ensembl.org/i/species/Bos_taurus.png',
    "Caenorhabditis elegans(6239)":'http://www.ensembl.org/i/species/Caenorhabditis_elegans.png',
    "Callithrix jacchus(9483)":'http://www.ensembl.org/i/species/Callithrix_jacchus.png',
    "Canis lupus dingo(286419)":'http://www.ensembl.org/i/species/Canis_lupus_dingo.png',
    "Canis lupus familiaris(9615)":'http://www.ensembl.org/i/species/Canis_familiaris.png',
    "Capra hircus(9925)":'http://www.ensembl.org/i/species/Capra_hircus.png',
    "Carlito syrichta(1868482)":'http://www.ensembl.org/i/species/Carlito_syrichta.png',
    "Cavia aperea(37548)":'http://www.ensembl.org/i/species/Cavia_aperea.png',
    "Cavia porcellus(10141)":'http://www.ensembl.org/i/species/Cavia_porcellus.png',
    "Cebus capucinus imitator(1737458)":'http://www.ensembl.org/i/species/Cebus_capucinus.png',
    "Cercocebus atys(9531)":'http://www.ensembl.org/i/species/Cercocebus_atys.png',
    "Chinchilla lanigera(34839)":'http://www.ensembl.org/i/species/Chinchilla_lanigera.png',
    "Chlorocebus sabaeus(60711)":'http://www.ensembl.org/i/species/Chlorocebus_sabaeus.png',
    "Choloepus hoffmanni(9358)":'http://www.ensembl.org/i/species/Choloepus_hoffmanni.png',
    "Chrysemys picta bellii(8478)":'http://www.ensembl.org/i/species/Chrysemys_picta_bellii.png',
    "Ciona intestinalis(7719)":'http://www.ensembl.org/i/species/Ciona_intestinalis.png',
    "Ciona savignyi(51511)":'http://www.ensembl.org/i/species/Ciona_savignyi.png',
    "Colobus angolensis palliatus(336983)":'http://www.ensembl.org/i/species/Colobus_angolensis_palliatus.png',
    "Cricetulus griseus(10029)":'http://www.ensembl.org/i/species/Cricetulus_griseus_chok1gshd.png',
    "Cynoglossus semilaevis(244447)":'http://www.ensembl.org/i/species/Cynoglossus_semilaevis.png',
    "Cyprinodon variegatus(28743)":'http://www.ensembl.org/i/species/Cyprinodon_variegatus.png',
    "Danio rerio(7955)":'http://www.ensembl.org/i/species/Danio_rerio.png',
    "Dasypus novemcinctus(9361)":'http://www.ensembl.org/i/species/Dasypus_novemcinctus.png',
    "Dipodomys ordii(10020)":'http://www.ensembl.org/i/species/Dipodomys_ordii.png',
    "Drosophila melanogaster(7227)":'http://www.ensembl.org/i/species/Drosophila_melanogaster.png',
    "Echinops telfairi(9371)":'http://www.ensembl.org/i/species/Echinops_telfairi.png',
    "Eptatretus burgeri(7764)":'http://www.ensembl.org/i/species/Eptatretus_burgeri.png',
    "Equus asinus asinus(83772)":'http://www.ensembl.org/i/species/Equus_asinus_asinus.png',
    "Equus caballus(9796)":'http://www.ensembl.org/i/species/Equus_caballus.png',
    "Erinaceus europaeus(9365)":'http://www.ensembl.org/i/species/Erinaceus_europaeus.png',
    "Esox lucius(8010)":'http://www.ensembl.org/i/species/Esox_lucius.png',
    "Felis catus(9685)":'http://www.ensembl.org/i/species/Felis_catus.png',
    "Ficedula albicollis(59894)":'http://www.ensembl.org/i/species/Ficedula_albicollis.png',
    "Fukomys damarensis(885580)":'http://www.ensembl.org/i/species/Fukomys_damarensis.png',
    "Fundulus heteroclitus(8078)":'http://www.ensembl.org/i/species/Fundulus_heteroclitus.png',
    "Gadus morhua(8049)":'http://www.ensembl.org/i/species/Gadus_morhua.png',
    "Gallus gallus(9031)":'http://www.ensembl.org/i/species/Gallus_gallus.png',
    "Gambusia affinis(33528)":'http://www.ensembl.org/i/species/Gambusia_affinis.png',
    "Gasterosteus aculeatus(69293)":'http://www.ensembl.org/i/species/Gasterosteus_aculeatus.png',
    "Gopherus agassizii(38772)":'http://www.ensembl.org/i/species/Gopherus_agassizii.png',
    "Gorilla gorilla gorilla(9595)":'http://www.ensembl.org/i/species/Gorilla_gorilla.png',
    "Haplochromis burtoni(8153)":'http://www.ensembl.org/i/species/Haplochromis_burtoni.png',
    "Heterocephalus glaber(10181)":'http://www.ensembl.org/i/species/Heterocephalus_glaber_female.png',
    "Hippocampus comes(109280)":'http://www.ensembl.org/i/species/Hippocampus_comes.png',
    "Homo sapiens(9606)":'http://www.ensembl.org/i/species/Homo_sapiens.png',
    "Ictalurus punctatus(7998)":'http://www.ensembl.org/i/species/Ictalurus_punctatus.png',
    "Ictidomys tridecemlineatus(43179)":'http://www.ensembl.org/i/species/Ictidomys_tridecemlineatus.png',
    "Jaculus jaculus(51337)":'http://www.ensembl.org/i/species/Jaculus_jaculus.png',
    "Kryptolebias marmoratus(37003)":'http://www.ensembl.org/i/species/Kryptolebias_marmoratus.png',
    "Labrus bergylta(56723)":'http://www.ensembl.org/i/species/Labrus_bergylta.png',
    "Latimeria chalumnae(7897)":'http://www.ensembl.org/i/species/Latimeria_chalumnae.png',
    "Lepisosteus oculatus(7918)":'http://www.ensembl.org/i/species/Lepisosteus_oculatus.png',
    "Loxodonta africana(9785)":'http://www.ensembl.org/i/species/Loxodonta_africana.png',
    "Macaca fascicularis(9541)":'http://www.ensembl.org/i/species/Macaca_fascicularis.png',
    "Macaca mulatta(9544)":'http://www.ensembl.org/i/species/Macaca_mulatta.png',
    "Macaca nemestrina(9545)":'http://www.ensembl.org/i/species/Macaca_nemestrina.png',
    "Mandrillus leucophaeus(9568)":'http://www.ensembl.org/i/species/Mandrillus_leucophaeus.png',
    "Mastacembelus armatus(205130)":'http://www.ensembl.org/i/species/Mastacembelus_armatus.png',
    "Maylandia zebra(106582)":'http://www.ensembl.org/i/species/Maylandia_zebra.png',
    "Meleagris gallopavo(9103)":'http://www.ensembl.org/i/species/Meleagris_gallopavo.png',
    "Mesocricetus auratus(10036)":'http://www.ensembl.org/i/species/Mesocricetus_auratus.png',
    "Microcebus murinus(30608)":'http://www.ensembl.org/i/species/Microcebus_murinus.png',
    "Microtus ochrogaster(79684)":'http://www.ensembl.org/i/species/Microtus_ochrogaster.png',
    "Mola mola(94237)":'http://www.ensembl.org/i/species/Mola_mola.png',
    "Monodelphis domestica(13616)":'http://www.ensembl.org/i/species/Monodelphis_domestica.png',
    "Monopterus albus(43700)":'http://www.ensembl.org/i/species/Monopterus_albus.png',
    "Mus caroli(10089)":'http://www.ensembl.org/i/species/Mus_caroli.png',
    "Mus musculus(10090)":'http://www.ensembl.org/i/species/Mus_musculus.png',
    "Mus musculus castaneus(10091)":'http://www.ensembl.org/i/species/Mus_musculus_CAST_EiJ.png',
    "Mus musculus domesticus(10092)":'http://www.ensembl.org/i/species/Mus_musculus_WSB_EiJ.png',
    "Mus musculus musculus(39442)":'http://www.ensembl.org/i/species/Mus_musculus_PWK_PhJ.png',
    "Mus pahari(10093)":'http://www.ensembl.org/i/species/Mus_pahari.png',
    "Mus spretus(10096)":'http://www.ensembl.org/i/species/Mus_spretus.png',
    "Mustela putorius furo(9669)":'http://www.ensembl.org/i/species/Mustela_putorius_furo.png',
    "Myotis lucifugus(59463)":'http://www.ensembl.org/i/species/Myotis_lucifugus.png',
    "Nannospalax galili(1026970)":'http://www.ensembl.org/i/species/Nannospalax_galili.png',
    "Neolamprologus brichardi(32507)":'http://www.ensembl.org/i/species/Neolamprologus_brichardi.png',
    "Nomascus leucogenys(61853)":'http://www.ensembl.org/i/species/Nomascus_leucogenys.png',
    "Notamacropus eugenii(9315)":'http://www.ensembl.org/i/species/Notamacropus_eugenii.png',
    "Ochotona princeps(9978)":'http://www.ensembl.org/i/species/Ochotona_princeps.png',
    "Octodon degus(10160)":'http://www.ensembl.org/i/species/Octodon_degus.png',
    "Oreochromis niloticus(8128)":'http://www.ensembl.org/i/species/Oreochromis_niloticus.png',
    "Ornithorhynchus anatinus(9258)":'http://www.ensembl.org/i/species/Ornithorhynchus_anatinus.png',
    "Oryctolagus cuniculus(9986)":'http://www.ensembl.org/i/species/Oryctolagus_cuniculus.png',
    "Oryzias latipes(8090)":'http://www.ensembl.org/i/species/Oryzias_latipes_hni.png',
    "Oryzias melastigma(30732)":'http://www.ensembl.org/i/species/Oryzias_melastigma.png',
    "Otolemur garnettii(30611)":'http://www.ensembl.org/i/species/Otolemur_garnettii.png',
    "Ovis aries(9940)":'http://www.ensembl.org/i/species/Ovis_aries.png',
    "Pan paniscus(9597)":'http://www.ensembl.org/i/species/Pan_paniscus.png',
    "Pan troglodytes(9598)":'http://www.ensembl.org/i/species/Pan_troglodytes.png',
    "Panthera pardus(9691)":'http://www.ensembl.org/i/species/Panthera_pardus.png',
    "Panthera tigris altaica(74533)":'http://www.ensembl.org/i/species/Panthera_tigris_altaica.png',
    "Papio anubis(9555)":'http://www.ensembl.org/i/species/Papio_anubis.png',
    "Paramormyrops kingsleyae(1676925)":'http://www.ensembl.org/i/species/Paramormyrops_kingsleyae.png',
    "Pelodiscus sinensis(13735)":'http://www.ensembl.org/i/species/Pelodiscus_sinensis.png',
    "Periophthalmus magnuspinnatus(409849)":'http://www.ensembl.org/i/species/Periophthalmus_magnuspinnatus.png',
    "Peromyscus maniculatus bairdii(230844)":'http://www.ensembl.org/i/species/Peromyscus_maniculatus_bairdii.png',
    "Petromyzon marinus(7757)":'http://www.ensembl.org/i/species/Petromyzon_marinus.png',
    "Phascolarctos cinereus(38626)":'http://www.ensembl.org/i/species/Phascolarctos_cinereus.png',
    "Poecilia formosa(48698)":'http://www.ensembl.org/i/species/Poecilia_formosa.png',
    "Poecilia latipinna(48699)":'http://www.ensembl.org/i/species/Poecilia_latipinna.png',
    "Poecilia mexicana(48701)":'http://www.ensembl.org/i/species/Poecilia_mexicana.png',
    "Poecilia reticulata(8081)":'http://www.ensembl.org/i/species/Poecilia_reticulata.png',
    "Pongo abelii(9601)":'http://www.ensembl.org/i/species/Pongo_abelii.png',
    "Procavia capensis(9813)":'http://www.ensembl.org/i/species/Procavia_capensis.png',
    "Propithecus coquereli(379532)":'http://www.ensembl.org/i/species/Propithecus_coquereli.png',
    "Pteropus vampyrus(132908)":'http://www.ensembl.org/i/species/Pteropus_vampyrus.png',
    "Pundamilia nyererei(303518)":'http://www.ensembl.org/i/species/Pundamilia_nyererei.png',
    "Pygocentrus nattereri(42514)":'http://www.ensembl.org/i/species/Pygocentrus_nattereri.png',
    "Rattus norvegicus(10116)":'http://www.ensembl.org/i/species/Rattus_norvegicus.png',
    "Rhinopithecus bieti(61621)":'http://www.ensembl.org/i/species/Rhinopithecus_bieti.png',
    "Rhinopithecus roxellana(61622)":'http://www.ensembl.org/i/species/Rhinopithecus_roxellana.png',
    "Saccharomyces cerevisiae(4932)":'http://www.ensembl.org/i/species/Saccharomyces_cerevisiae.png',
    "Saimiri boliviensis boliviensis(39432)":'http://www.ensembl.org/i/species/Saimiri_boliviensis_boliviensis.png',
    "Sarcophilus harrisii(9305)":'http://www.ensembl.org/i/species/Sarcophilus_harrisii.png',
    "Scleropages formosus(113540)":'http://www.ensembl.org/i/species/Scleropages_formosus.png',
    "Scophthalmus maximus(52904)":'http://www.ensembl.org/i/species/Scophthalmus_maximus.png',
    "Seriola dumerili(41447)":'http://www.ensembl.org/i/species/Seriola_dumerili.png',
    "Seriola lalandi dorsalis(1841481)":'http://www.ensembl.org/i/species/Seriola_lalandi_dorsalis.png',
    "Sorex araneus(42254)":'http://www.ensembl.org/i/species/Sorex_araneus.png',
    "Sphenodon punctatus(8508)":'http://www.ensembl.org/i/species/Sphenodon_punctatus.png',
    "Stegastes partitus(144197)":'http://www.ensembl.org/i/species/Stegastes_partitus.png',
    "Sus scrofa(9823)":'http://www.ensembl.org/i/species/Sus_scrofa.png',
    "Taeniopygia guttata(59729)":'http://www.ensembl.org/i/species/Taeniopygia_guttata.png',
    "Takifugu rubripes(31033)":'http://www.ensembl.org/i/species/Takifugu_rubripes.png',
    "Tetraodon nigroviridis(99883)":'http://www.ensembl.org/i/species/Tetraodon_nigroviridis.png',
    "Tupaia belangeri(37347)":'http://www.ensembl.org/i/species/Tupaia_belangeri.png',
    "Tursiops truncatus(9739)":'http://www.ensembl.org/i/species/Tursiops_truncatus.png',
    "Ursus americanus(9643)":'http://www.ensembl.org/i/species/Ursus_americanus.png',
    "Ursus maritimus(29073)":'http://www.ensembl.org/i/species/Ursus_maritimus.png',
    "Vicugna pacos(30538)":'http://www.ensembl.org/i/species/Vicugna_pacos.png',
    "Vulpes vulpes(9627)":'http://www.ensembl.org/i/species/Vulpes_vulpes.png',
    "Xenopus tropicalis(8364)":'http://www.ensembl.org/i/species/Xenopus_tropicalis.png',
    "Xiphophorus couchianus(32473)":'http://www.ensembl.org/i/species/Xiphophorus_couchianus.png',
    "Xiphophorus maculatus(8083)":'http://www.ensembl.org/i/species/Xiphophorus_maculatus.png',
        },
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
                    $('.modal').modal('close');
                }, error: function (error) {
                    console.log(error);
                    $('#genesequence').val(error.responseText);
                    $('.modal').modal('close');
                }
            });
        }
    });

    $('#load-example-data').click(function () {
        $('#genesequence').load('assets/data/Ecoli_511145.fasta');
        $('#genesequence').addClass('active');
        $('#organismName').val('Escherichia coli str. K-12 substr. MG1655 (511145)');
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
                        $('select').material_select();
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

