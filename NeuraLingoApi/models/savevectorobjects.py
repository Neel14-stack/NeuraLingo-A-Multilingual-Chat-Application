import os
import re
import string
from datasetclass import Dataset
import config
import tensorflow as tf
from keras.layers import TextVectorization
import pickle

class Vectorization(Dataset):

    @staticmethod
    def custom_standardization(input_string):
        strip_chars = string.punctuation + "¿"
        strip_chars = strip_chars.replace("[", "")
        strip_chars = strip_chars.replace("]", "")
        lowercase = tf.strings.lower(input_string)
        return tf.strings.regex_replace(lowercase, "[%s]" % re.escape(strip_chars), "")

    def save_vectors(self):
        self.vectors[config.DEU_LANG] = TextVectorization(
            max_tokens=config.vocab_size,
            output_mode="int",
            output_sequence_length=config.sequence_length)
        self.vectors["output_"+config.DEU_LANG] = TextVectorization(
            max_tokens=config.vocab_size,
            output_mode="int",
            output_sequence_length=config.sequence_length+1,
            standardize=self.custom_standardization)
        self.vectors[config.ENG_LANG] = TextVectorization(
            max_tokens=config.vocab_size,
            output_mode="int",
            output_sequence_length=config.sequence_length)
        self.vectors["output_" + config.ENG_LANG] = TextVectorization(
            max_tokens=config.vocab_size,
            output_mode="int",
            output_sequence_length=config.sequence_length + 1,
            standardize=self.custom_standardization)
        for key in self.vectors:
            already_made = os.listdir("VectorObjects")
            if key + ".pkl" not in already_made:
                print("Creating Vectors for " + key)
                self.vectors.get(key).adapt(self.get_dataset(key))
                print(self.vectors.get(key).get_vocabulary())
                pickle.dump({"config": self.vectors.get(key).get_config,
                             "weights": self.vectors.get(key).get_weights(),
                             "vocabulary": self.vectors.get(key).get_vocabulary()},
                            open("VectorObjects/" + key + ".pkl", 'wb'))

    def __init__(self):
        super().__init__()
        self.vectors = {}


if __name__ == "__main__":
    obj = Vectorization()
    obj.save_vectors()
