<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>ORFanGenes - Results</title>
    <link rel="shortcut icon" type="image/x-icon" href="assets/favicon.ico" />
    <!--Import Google Icon Font-->
    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet" href="assets/css/materialize.min.css" media="screen,projection" />
    <link type="text/css" rel="stylesheet" href="assets/css/orfanid-input.css">
    <link type="text/css" rel="stylesheet" href="assets/css/orfan_styles.css">
    <link type="text/css" rel="stylesheet" href="https://ebi.emblstatic.net/web_guidelines/EBI-Icon-fonts/v1.2/fonts.css">
    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <!--Import jQuery before materialize.js-->
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="assets/js/materialize.min.js"></script>
    <script type="text/javascript" src="assets/js/plotly-latest.min.js"></script>
    <script type="text/javascript" src="assets/js/orfanid-input.js"></script>
    <script type="text/javascript" src="assets/js/orfanid-results.js"></script>
    <script type="text/javascript" src="assets/js/jquery.dataTables.min.js"></script>
</head>
<body>
<nav>
    <div class="nav-wrapper teal">
        <a href="/" class="brand-logo">ORFanID</a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li><a href="/input">Home</a></li>
            <li><a href="/results">Results</a></li>
            <li><a href="instructions">Instructions</a></li>
        </ul>
    </div>
</nav>
<main>
    <div class="section">
        <div id="savedResultView" class="row">
            <div class="col s10 offset-s1 center-align">

                <table id="ResultViewTable" class="display" width="100%" cellspacing="0">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Result ID</th>
                        <th>E Mail</th>
                        <th>Organism</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tfoot>
                    <tr>
                        <th>Date</th>
                        <th>Result ID</th>
                        <th>E Mail</th>
                        <th>Organism</th>
                        <th></th>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>
</main>
<footer class="page-footer teal lighten-1">
    <div class="container">
        <div class="row">
            <div class="col s12">
                <h5 class="white-text">ORFanID</h5>
                <span valign-wrapper>
        <p class="grey-text text-lighten-4">ORFanID is an open-source web application
          that is capable of predicting orphan genes from a given list of gene sequences of a specified species.
          Each candidate gene is searched against the BLAST non-redundant database
          to find any detectable homologous sequences using BLASP programme.
          </p>
      </span>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © Copyright 2017-2018
            <a class="grey-text text-lighten-4 right" href="mailto:sureshhewabi@gmail.com?cc=vinodh@thegunasekeras.org,richgun01@gmail.com&Subject=ORFanID%20Online%20Issues">Contact Us</a>
        </div>
    </div>
</footer>
</body>
</html>