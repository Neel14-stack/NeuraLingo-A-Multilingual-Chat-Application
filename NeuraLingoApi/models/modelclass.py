from concurrent.futures import ProcessPoolExecutor, ThreadPoolExecutor
import numpy as np
from . import config
from . vectorizationclass import Vectorization
import traceback

class ModelClass(Vectorization):

    def decode_sequence(self, data):
        try:
            input_lang_vector = self.vectors["input_" + data["key"].split("-")[0]]["vector"]
            output_lang_vector = self.vectors["output_" + data["key"].split("-")[1]]["vector"]
            output_lookup = self.vectors["output_" + data["key"].split("-")[1]]["lookup"]
            tokenized_input_sentence = input_lang_vector([data["message"]])
            decoded_sentence = "[start]"
            print("Starting Decoding")
            for i in range(config.max_decoded_sentence_length):
                tokenized_target_sentence = output_lang_vector([decoded_sentence])[:, :-1]
                predictions = self.models[data["key"]]([tokenized_input_sentence, tokenized_target_sentence])
                sampled_token_index = np.argmax(predictions[0, i, :])
                sampled_token = output_lookup[sampled_token_index]
                decoded_sentence += " " + sampled_token
                if sampled_token == "[end]":
                    break
            print("Decoded Seq " + decoded_sentence)
            decoded_sentence = decoded_sentence.replace("[start] ", "")
            decoded_sentence = decoded_sentence.replace(" [end]", "")
            self.final_output_dict[data["key"]] = decoded_sentence
        except Exception:
            print(traceback.format_exc())

    def translate(self, trans_pair_dict, message):
        with ThreadPoolExecutor() as executor:
            for key in trans_pair_dict:
                data = {"key": key, "message": message}
                executor.submit(self.decode_sequence, data)
                print(key)
        print("Done")
        return self.final_output_dict

    def __init__(self, models):
        super().__init__()
        self.load_vectors()
        self.models = models
        self.final_output_dict = {}

# if __name__ == "__main__":
#     translateObj = ModelClass()
#     translateObj.translate({"eng-ger", "ger-eng"})
