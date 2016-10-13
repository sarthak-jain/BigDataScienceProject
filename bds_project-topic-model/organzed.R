library(coreNLP)
library(dplyr)
library(tm)
library(SnowballC)
library(wordcloud)
library(Matrix)

set.seed(0)

########################################################################## load list of positive and negative words from Hu and Liu's 2004 paper 
setwd("/Users/wonikJang/Desktop/Stern_2nd/big_data/bigdata_project")
pos_words <- scan("positive-words.txt", character(), comment.char=";")
neg_words <- scan("negative-words.txt", character(), comment.char=";")


########################################################################## load review file
review1<-fread('Review1.csv', header = FALSE, sep = ',')
colnames(review1)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review1$content)

########################################################################## CoreNLP tagging and Word-Cloud
reviewtext<-gsub("[[:punct:][:digit:]]", "", text )
#length(reviewtext)
head(reviewtext)
initCoreNLP(mem="8g", annotators= c("tokenize","ssplit","pos","lemma"))
bagOfWords <- rep("", length(reviewtext))
for (j in seq_along(reviewtext)) {
  s<-reviewtext[j]
  anno<-annotateString(s)
  token<-getToken(anno)
  theseLemma <- token$lemma[token$POS %in% c("JJ","JJR","JJS","NNS","NN")]
  bagOfWords[j] <- paste(theseLemma, collapse=" ")
}
head(bagOfWords)
length(bagOfWords)
jeopCorpus <- Corpus(VectorSource(bagOfWords))
jeopCorpus <- tm_map(jeopCorpus, PlainTextDocument)
# Remove the 'place', which doesn't mean anything
jeopCorpus <- tm_map(jeopCorpus, removeWords, c('place','food','restaur'))
# Eliminate extra white spaces
jeopCorpus <- tm_map(jeopCorpus , stripWhitespace)
jeopCorpus <- tm_map(jeopCorpus, stemDocument)
dtm <- TermDocumentMatrix(jeopCorpus)

library(slam)
m <- rollup(dtm, 2, na.rm=TRUE, FUN = sum)
m <- as.matrix(m)
v <- sort(rowSums(m),decreasing=TRUE)
d <- data.frame(word = names(v),freq=v)
head(d, 10)

wordcloud(words = d$word, freq = d$freq, min.freq = 1,
          max.words=200, random.order=FALSE, rot.per=0.35, 
          colors=brewer.pal(8, "Dark2"))

########################################################################## Count number of positive and negative words of review for each restaurant 
corpus <- Corpus(VectorSource(bagOfWords))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1

review1$pos_count <- as.numeric(dtm %*% pos_wt)
review1$neg_count <- as.numeric(dtm %*% neg_wt)##
review1$total_word<-review1$pos_count+review1$neg_count



