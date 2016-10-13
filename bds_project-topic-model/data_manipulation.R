library("coreNLP")
library(data.table)
library(dplyr)
library(tm)
library(SnowballC)
library(wordcloud)
library(Matrix)

set.seed(0)
setwd("/Users/wonikJang/Desktop/Stern_2nd/big_data/bigdata_project")
pos_words <- scan("positive-words.txt", character(), comment.char=";")
neg_words <- scan("negative-words.txt", character(), comment.char=";")

review1<-fread('Review1.csv', header = FALSE, sep = ',')
colnames(review1)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review1$content)


##review1$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review1$text))
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


############################################### Word-cloud example

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

############################################################################

review2<-fread('Review2.csv', header = FALSE, sep = ',')
colnames(review2)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review2$content)
review2$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review2$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review2$pos_count <- as.numeric(dtm %*% pos_wt)
review2$neg_count <- as.numeric(dtm %*% neg_wt)

review3<-fread('Review3.csv', header = FALSE, sep = ',')
colnames(review3)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review3$content)
review3$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review3$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review3$pos_count <- as.numeric(dtm %*% pos_wt)
review3$neg_count <- as.numeric(dtm %*% neg_wt)

review4<-fread('Review4.csv', header = FALSE, sep = ',')
colnames(review4)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review4$content)
review4$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review4$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review4$pos_count <- as.numeric(dtm %*% pos_wt)
review4$neg_count <- as.numeric(dtm %*% neg_wt)

review5<-fread('review5.csv', header = FALSE, sep = ',')
colnames(review5)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review5$content)
review5$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review5$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review5$pos_count <- as.numeric(dtm %*% pos_wt)
review5$neg_count <- as.numeric(dtm %*% neg_wt)

review6<-fread('Review6.csv', header = FALSE, sep = ',')
colnames(review6)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review6$content)
review6$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review6$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review6$pos_count <- as.numeric(dtm %*% pos_wt)
review6$neg_count <- as.numeric(dtm %*% neg_wt)

review7<-fread('Review7.csv', header = FALSE, sep = ',')
colnames(review7)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review7$content)
review7$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review7$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review7$pos_count <- as.numeric(dtm %*% pos_wt)
review7$neg_count <- as.numeric(dtm %*% neg_wt)

review8<-fread('Review8.csv', header = FALSE, sep = ',')
colnames(review8)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review8$content)
review8$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review8$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review8$pos_count <- as.numeric(dtm %*% pos_wt)
review8$neg_count <- as.numeric(dtm %*% neg_wt)

review9<-fread('Review9.csv', header = FALSE, sep = ',')
colnames(review9)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review9$content)
review9$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review9$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review9$pos_count <- as.numeric(dtm %*% pos_wt)
review9$neg_count <- as.numeric(dtm %*% neg_wt)

review10<-fread('Review10.csv', header = FALSE, sep = ',')
colnames(review10)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review10$content)
review10$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review10$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review10$pos_count <- as.numeric(dtm %*% pos_wt)
review10$neg_count <- as.numeric(dtm %*% neg_wt)

review11<-fread('Review11.csv', header = FALSE, sep = ',')
colnames(review11)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review11$content)
review11$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review11$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review11$pos_count <- as.numeric(dtm %*% pos_wt)
review11$neg_count <- as.numeric(dtm %*% neg_wt)

review12<-fread('Review12.csv', header = FALSE, sep = ',')
colnames(review12)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review12$content)
review12$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review12$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review12$pos_count <- as.numeric(dtm %*% pos_wt)
review12$neg_count <- as.numeric(dtm %*% neg_wt)

review13<-fread('Review13.csv', header = FALSE, sep = ',')
colnames(review13)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review13$content)
review13$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review13$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review13$pos_count <- as.numeric(dtm %*% pos_wt)
review13$neg_count <- as.numeric(dtm %*% neg_wt)

review14<-fread('Review14.csv', header = FALSE, sep = ',')
colnames(review14)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review14$content)
review14$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review14$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review14$pos_count <- as.numeric(dtm %*% pos_wt)
review14$neg_count <- as.numeric(dtm %*% neg_wt)

review15<-fread('Review15.csv', header = FALSE, sep = ',')
colnames(review15)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review15$content)
review15$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review15$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review15$pos_count <- as.numeric(dtm %*% pos_wt)
review15$neg_count <- as.numeric(dtm %*% neg_wt)

review16<-fread('Review16.csv', header = FALSE, sep = ',')
colnames(review16)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review16$content)
review16$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review16$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review16$pos_count <- as.numeric(dtm %*% pos_wt)
review16$neg_count <- as.numeric(dtm %*% neg_wt)

review17<-fread('review17.csv', header = FALSE, sep = ',')
colnames(review17)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review17$content)
review17$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review17$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review17$pos_count <- as.numeric(dtm %*% pos_wt)
review17$neg_count <- as.numeric(dtm %*% neg_wt)

review18<-fread('Review18.csv', header = FALSE, sep = ',')
colnames(review18)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review18$content)
review18$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review18$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review18$pos_count <- as.numeric(dtm %*% pos_wt)
review18$neg_count <- as.numeric(dtm %*% neg_wt)

review19<-fread('Review19.csv', header = FALSE, sep = ',')
colnames(review19)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review19$content)
review19$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review19$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review19$pos_count <- as.numeric(dtm %*% pos_wt)
review19$neg_count <- as.numeric(dtm %*% neg_wt)

review20<-fread('Review20.csv', header = FALSE, sep = ',')
colnames(review20)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review20$content)
review20$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review20$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review20$pos_count <- as.numeric(dtm %*% pos_wt)
review20$neg_count <- as.numeric(dtm %*% neg_wt)

review21<-fread('Review21.csv', header = FALSE, sep = ',')
colnames(review21)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review21$content)
review21$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review21$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review21$pos_count <- as.numeric(dtm %*% pos_wt)
review21$neg_count <- as.numeric(dtm %*% neg_wt)

review22<-fread('Review-extra3.csv', header = FALSE, sep = ',')
colnames(review22)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review22$content)
review22$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review22$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review22$pos_count <- as.numeric(dtm %*% pos_wt)
review22$neg_count <- as.numeric(dtm %*% neg_wt)

review23<-fread('Review-extra12.csv', header = FALSE, sep = ',')
colnames(review23)<-c('businessId','reviewIdx','author','authorId','ratingValue','datePublished','content','useful','funny','cool')
library("stringi")
text<- stringi::stri_trans_nfkc_casefold(review23$content)
review23$text<-gsub("[[:punct:][:digit:]]", "", text )
corpus <- VCorpus(VectorSource(review23$text))
control <- list(tolower = TRUE, removePunctuation = TRUE,removeNumbers = TRUE, wordLengths=c(1, Inf))
dtm <- DocumentTermMatrix(corpus, control=control)
dtm <- sparseMatrix(dtm$i, dtm$j, x = dtm$v, dim=dim(dtm), dimnames=dimnames(dtm))
vocab <- colnames(dtm)
nvocab <- length(vocab)
pos_wt <- numeric(nvocab)
pos_wt[match(pos_words, vocab, 0)] <- 1
neg_wt <- numeric(nvocab)
neg_wt[match(neg_words, vocab, 0)] <- 1
review23$pos_count <- as.numeric(dtm %*% pos_wt)
review23$neg_count <- as.numeric(dtm %*% neg_wt)

MyMerge <- function(x, y){df <- merge(x, y, by= "businessId", all.x= TRUE, all.y= TRUE) 
                    return(df) }
############################## Merge into business
rev1pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev1neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business1<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business1)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business1all <- Reduce(MyMerge, list(rev1pos,rev1neg,business1))

rev2pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev2neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business2<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business2)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business2all <- Reduce(MyMerge, list(rev2pos,rev2neg,business2))

rev3pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev3neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business3<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business3)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business3all <- Reduce(MyMerge, list(rev3pos,rev3neg,business3))

rev4pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev4neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business4<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business4)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business4all <- Reduce(MyMerge, list(rev4pos,rev4neg,business4))

rev5pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev5neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business5<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business5)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business5all <- Reduce(MyMerge, list(rev5pos,rev5neg,business5))

rev6pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev6neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business6<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business6)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business6all <- Reduce(MyMerge, list(rev6pos,rev6neg,business6))

rev7pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev7neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business7<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business7)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business7all <- Reduce(MyMerge, list(rev7pos,rev7neg,business7))

rev8pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev8neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business8<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business8)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business8all <- Reduce(MyMerge, list(rev8pos,rev8neg,business8))

rev9pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev9neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business9<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business9)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business9all <- Reduce(MyMerge, list(rev9pos,rev9neg,business9))

rev10pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev10neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business10<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business10)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business10all <- Reduce(MyMerge, list(rev10pos,rev10neg,business10))

rev11pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev11neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business11<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business11)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business11all <- Reduce(MyMerge, list(rev11pos,rev11neg,business11))

rev12pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev12neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business12<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business12)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business12all <- Reduce(MyMerge, list(rev12pos,rev12neg,business12))

rev13pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev13neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business13<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business13)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business13all <- Reduce(MyMerge, list(rev13pos,rev13neg,business13))

rev14pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev14neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business14<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business14)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business14all <- Reduce(MyMerge, list(rev14pos,rev14neg,business14))

rev15pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev15neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business15<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business15)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business15all <- Reduce(MyMerge, list(rev15pos,rev15neg,business15))

rev16pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev16neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business16<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business16)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business16all <- Reduce(MyMerge, list(rev16pos,rev16neg,business16))

rev17pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev17neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business17<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business17)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business17all <- Reduce(MyMerge, list(rev17pos,rev17neg,business17))

rev18pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev18neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business18<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business18)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business18all <- Reduce(MyMerge, list(rev18pos,rev18neg,business18))

rev19pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev19neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business19<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business19)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business19all <- Reduce(MyMerge, list(rev19pos,rev19neg,business19))

rev20pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev20neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business20<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business20)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business20all <- Reduce(MyMerge, list(rev20pos,rev20neg,business20))

rev21pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev21neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business21<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business21)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business21all <- Reduce(MyMerge, list(rev21pos,rev21neg,business21))

rev22pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev22neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business22<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business22)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business22all <- Reduce(MyMerge, list(rev22pos,rev22neg,business22))

rev23pos<-review1 %>% group_by(businessId) %>%  summarise(positive = sum(pos_count))
rev23neg<-review1 %>% group_by(businessId) %>%  summarise(negative = sum(neg_count))
business23<-fread('Restaurant1.csv', header = FALSE, sep = ',')
colnames(business23)<-c('businessId','name','address','phone','price','reviewCount','rating','category','openHour','yelpURL')
business23all <- Reduce(MyMerge, list(rev23pos,rev23neg,business23))

## Join all business 
businessall<-do.call("rbind", list(business1all,business2all,business3all,business4all,business5all,business6all,business7all,business8all,business9all,business10all,business11all,
                      business12all,business13all,business14all,business15all,business16all,business17all,business18all,business19all,business20all,business21all,
                      business22all,business23all))
write.csv(businessall,"businessall.csv")

## Classification 
head(businessall2)
businessall<-data.frame(businessall)
businessall2<-businessall[,c('positive','negative','reviewCount','rating')]
dim(businessall2)
businessall3<-businessall2[complete.cases(businessall2),]
dim(businessall3)
kc <- kmeans(businessall3, 5)
plot(businessall3[c('rating','reviewCount')], col=kc$cluster)
points(kc$centers[,c('rating','reviewCount')], col=1:5, pch=8, cex=2)

businessall4<-businessall3[,c('positive','negative')]
kc <- kmeans(businessall4, 5)
plot(businessall4[c('positive','negative')], col=kc$cluster)
points(kc$centers[,c('positive','negative')], col=1:5, pch=8, cex=2)















