echo "Downloading Taxdump"
curl -OS "ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/new_taxdump/new_taxdump.tar.gz"
currentDir=$PWD
if [[ -f new_taxdump.tar.gz ]]
then
    mkdir new_taxdump
    cp new_taxdump.tar.gz new_taxdump
    cd new_taxdump
    echo "Extracting Taxdump"
    gunzip new_taxdump.tar.gz
    tar xvpf new_taxdump.tar
else
    echo "BLAST+ to download taxdump"
fi
echo "Copying rankedlineage.dmp to resources"
cp ${currentDir}/new_taxdump/rankedlineage.dmp ${currentDir}/../src/main/resources
rm -rf ${currentDir}/new_taxdump
echo "Setup complete"