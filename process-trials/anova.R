library(ez)

Dataset <- read.table("/Users/kenhualiew/Desktop/process-trials/cleantrials/ANOVAdata.csv",
  header=TRUE, sep=",", na.strings="NA", dec=".", strip.white=TRUE)

Block1 <- read.table("/Users/kenhualiew/Desktop/process-trials/cleantrials/ANOVAdataB1.csv",
  header=TRUE, sep=",", na.strings="NA", dec=".", strip.white=TRUE)

Block2 <- read.table("/Users/kenhualiew/Desktop/process-trials/cleantrials/ANOVAdataB2.csv",
  header=TRUE, sep=",", na.strings="NA", dec=".", strip.white=TRUE)

Block3 <- read.table("/Users/kenhualiew/Desktop/process-trials/cleantrials/ANOVAdataB3.csv",
  header=TRUE, sep=",", na.strings="NA", dec=".", strip.white=TRUE)

ezANOVA(data=Dataset, dv=time, wid=pid, within=c(technique, granularity, windows), detailed=TRUE)
ezANOVA(data=Dataset, dv=accuracy, wid=pid, within=c(technique, granularity, windows), detailed=TRUE)
ezANOVA(data=Dataset, dv=steps, wid=pid, within=c(technique, granularity, windows), detailed=TRUE)

library(mvtnorm)
library(survival)
library(MASS)
library(TH.data)
library(multcomp)
library(abind)

anova1 <- aov(time ~ granularity, data=Dataset)
summary(anova1)
with(Dataset, numSummary(time, groups=granularity, statistics=c("mean", "sd")))
local({
  .Pairs <- glht(anova1, linfct = mcp(granularity = "Tukey"))
  print(summary(.Pairs)) # pairwise tests
  print(confint(.Pairs)) # confidence intervals
  print(cld(.Pairs)) # compact letter display
  old.oma <- par(oma=c(0,5,0,0))
  plot(confint(.Pairs))
  par(old.oma)
})
anova2 <- aov(time ~ windows, data=Dataset)
summary(anova2)
with(Dataset, numSummary(time, groups=windows, statistics=c("mean", "sd")))
local({
  .Pairs <- glht(anova2, linfct = mcp(windows = "Tukey"))
  print(summary(.Pairs)) # pairwise tests
  print(confint(.Pairs)) # confidence intervals
  print(cld(.Pairs)) # compact letter display
  old.oma <- par(oma=c(0,5,0,0))
  plot(confint(.Pairs))
  par(old.oma)
})
anova3 <- aov(accuracy ~ technique, data=Dataset)
summary(anova3)
with(Dataset, numSummary(accuracy, groups=technique, statistics=c("mean", "sd")))
local({
  .Pairs <- glht(anova3, linfct = mcp(technique = "Tukey"))
  print(summary(.Pairs)) # pairwise tests
  print(confint(.Pairs)) # confidence intervals
  print(cld(.Pairs)) # compact letter display
  old.oma <- par(oma=c(0,5,0,0))
  plot(confint(.Pairs))
  par(old.oma)
})
anova4 <- aov(accuracy ~ granularity, data=Dataset)
summary(anova4)
with(Dataset, numSummary(accuracy, groups=granularity, statistics=c("mean", "sd")))
local({
  .Pairs <- glht(anova4, linfct = mcp(granularity = "Tukey"))
  print(summary(.Pairs)) # pairwise tests
  print(confint(.Pairs)) # confidence intervals
  print(cld(.Pairs)) # compact letter display
  old.oma <- par(oma=c(0,5,0,0))
  plot(confint(.Pairs))
  par(old.oma)
})
anova5 <- aov(steps ~ granularity, data=Dataset)
summary(anova5)
with(Dataset, numSummary(steps, groups=granularity, statistics=c("mean", "sd")))
local({
  .Pairs <- glht(anova5, linfct = mcp(granularity = "Tukey"))
  print(summary(.Pairs)) # pairwise tests
  print(confint(.Pairs)) # confidence intervals
  print(cld(.Pairs)) # compact letter display
  old.oma <- par(oma=c(0,5,0,0))
  plot(confint(.Pairs))
  par(old.oma)
})


with(Dataset, plotMeans(time, technique, granularity, error.bars="se", connect=TRUE, legend.pos="farright"))
with(Dataset, (tapply(time, list(granularity, technique), mean, na.rm=TRUE))) # means
with(Dataset, (tapply(time, list(granularity, technique), sd, na.rm=TRUE))) # std. deviations

with(Dataset, plotMeans(time, technique, windows, error.bars="se", connect=TRUE, legend.pos="farright"))
with(Dataset, (tapply(time, list(windows, technique), mean, na.rm=TRUE))) # means
with(Dataset, (tapply(time, list(windows, technique), sd, na.rm=TRUE))) # std. deviations

with(Dataset, plotMeans(accuracy, technique, granularity, error.bars="se", connect=TRUE, legend.pos="farright"))
with(Dataset, (tapply(accuracy, list(granularity, technique), mean, na.rm=TRUE))) # means
with(Dataset, (tapply(accuracy, list(granularity, technique), sd, na.rm=TRUE))) # std. deviations

with(Dataset, plotMeans(steps, technique, granularity, error.bars="se", connect=TRUE, legend.pos="farright"))
with(Dataset, (tapply(steps, list(granularity, technique), mean, na.rm=TRUE))) # means
with(Dataset, (tapply(steps, list(granularity, technique), sd, na.rm=TRUE))) # std. deviations
