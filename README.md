# HierarchIcal based Motif Enumeration (HIME)

Website: https://sites.google.com/site/himeicdm/


## Getting started

To download and run the executable jar with demo:

```bash
git -clone https://github.com/flash121123/HIME.git
cd HIME/bin
. demo.sh
```

res.txt is the motif discovery result. 


## Output Format

[First Instance Start Location][First Instance End Location][Second Instance Start Location][Second Instance End Location][Length][Distance]


## How to Visualize Result

The matlab visualization code/tested dataset/screenshot are in  http://bit.ly/2rvBETV

TO VISUALIZE DEMO IN MATLAB:

```bash
plotVLMotif('demo.txt','res.txt');
```

or 

```bash
plotVLMotif('demo.txt','res.txt’, [Motif Threshold You Want to Test]);
```

## Run Your Own Data:

Using Default Parameter (start enumeration from length 300):

```bash
java -Xmx8g -jar HIME_release.jar [DATASET] > tmp.log
```

Choosing minimum length start enumeration:

```bash
java -Xmx8g -jar HIME_release.jar [DATASET] [MINIMUM LENGTH] > tmp.log
```

Choosing PAA and minimum length:

```bash
java -Xmx8g -jar HIME_release.jar [DATASET] [PAA] [MINIMUM LENGTH]  > tmp.log
```


Choosing PAA, Alphabet Size and minimum length:

```bash
java -Xmx8g -jar HIME_release.jar [DATASET] [PAA] [MINIMUM LENGTH] [ALPHABET SIZE]  > tmp.log
```

Choosing PAA, Alphabet Size, minimum length and R(L)=xL:

```bash
java -Xmx8g -jar HIME_release.jar [DATASET] [PAA] [MINIMUM LENGTH] [ALPHABET SIZE] [x]  > tmp.log
```


TO CONVERT LOGS TO RESULT DATA:

```bash
grep -i "Motif" tmp.log | cut -d' ' -f 2- > res.txt
```

## Other Notes:

1. The main process does not drop false positive motifs so res.txt may contain false positive. The threshold parametr is only used for discretization step in the program (See paper for detail). You can filter out all false positive based on the output result (distance metric for each motif).

2. The algorithm returns pair of variable length motif instances. You can use MASS algorithm in http://www.cs.unm.edu/~mueen/FastestSimilaritySearch.html to find exact instances. You may use MDL metric, original threshold or result-driven motif threshold to locate the occuring instances of motif.


## Reference

If you found the code is useful, please cite the paper

```
  @INPROCEEDINGS{hime2017, 
                 author={Y. Gao and J. Lin}, 
                 booktitle={2017 IEEE International Conference on Data Mining (ICDM)}, 
                 title={Efficient discovery of time series motifs with large length range in million scale time series}, 
                 year={2017}, 
                 pages={1213-1222}, 
                 month={Nov}
                 }
```

Dataset References can be found in the paper.



